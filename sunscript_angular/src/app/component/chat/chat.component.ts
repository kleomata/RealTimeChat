import { Component, OnInit, OnDestroy, Inject, PLATFORM_ID, ChangeDetectorRef, NgZone, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Message, WebsocketService } from '../../authentication/websocket.service';
import { Subscription } from 'rxjs';
import { isPlatformBrowser, NgFor, NgClass, NgIf, CommonModule, formatDate } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AllFollowingUserResponse, AuthService, GetUserResponse, UserStatusResponse } from '../../authentication/auth.service';
import { blob } from 'node:stream/consumers';
import { DomSanitizer } from '@angular/platform-browser';

import {Emoji, Picker} from 'emoji-mart'

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, NgFor, NgIf, NgClass, CommonModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent implements OnInit, OnDestroy{

  recipient: string = '';
  content: string = '';
 // messageContent: string = '';
  messages: Message[] = [];

  constructor(
    private route: ActivatedRoute,
    private webSocketService: WebsocketService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef, 
    private ngZone: NgZone,
    private sanitizer: DomSanitizer,
  ) {}
  private subscription: Subscription | null = null


  //userStatus: string = '';

  //users: any[] = [];

 
  private userStatusSubscription: Subscription | undefined;
  onlineUser: boolean = false
  lastSeenTime: Date | null = null

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.recipient = params['u'] || '';
        if (this.recipient) {
        this.loadUser();
        this.loadChatHistory();
        this.loadRecipient();

        this.getRecipientStatus(this.recipient)

        this.userStatusSubscription = this.webSocketService.getUseStatus().subscribe(status => {
          if (status[this.recipient] !== undefined) {
            
            if (status[this.recipient] === true) {
              this.onlineUser = true
            } else {
              if (this.onlineUser === true) {
                this.lastSeenTime = new Date();
              }
              this.onlineUser = false
            }
            
            
            //this.onlineUser = status[this.recipient]
          }
        })
      }
    });
    //this.subscriptionToMessages()
  }


  statusRecipient: UserStatusResponse | null = null;
  getRecipientStatus(username: string): void {
    this.authService.getRecipientStatus(username).subscribe(
      (data: UserStatusResponse) => {
        console.log("User status response:", data);
        this.statusRecipient = data;
        this.onlineUser = data.online
        this.lastSeenTime = data.lastOnlineTime ? new Date(data.lastOnlineTime) : null
      },
      error => {
        console.error("Error fetching user status:", error);
      }
    );
  }

  formatLastSeenTime() {
    if (!this.lastSeenTime) {
      return '';
    }

    const today = new Date();
    const lastSeen = new Date(this.lastSeenTime);

    const todayDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    const lastSeenDate = new Date(lastSeen.getFullYear(), lastSeen.getMonth(), lastSeen.getDate());

    const timeDiff = todayDate.getTime() - lastSeenDate.getTime();
    const dayDiff = Math.floor(timeDiff / (1000 * 60 * 60 * 24));

    if (dayDiff === 0) {
      return `${formatDate(lastSeen, 'HH:mm', 'en-US')}`;
    }

    else if (dayDiff === 1) {
      return `Yesterday ${formatDate(lastSeen, 'HH:mm', 'en-US')}`;
    }

    else if (dayDiff < 7) {
      const dayName = formatDate(lastSeen, 'EEEE', 'en-US');
      return `${dayName} ${formatDate(lastSeen, 'HH:mm', 'en-US')}`;
    }

    else {
      return `${formatDate(lastSeen, 'MMM d, yyyy - HH:mm', 'en-US')}`;
    }
  }

  @ViewChild('messageContainer') private messageContainer!: ElementRef;
  private scrollToBottom(): void {
    try {
      setTimeout(() => {
        if (this.messageContainer) {
          this.messageContainer.nativeElement.scrollTop = this.messageContainer.nativeElement.scrollHeight;
        }
      }, 300);
    } catch (err) {
      console.error('Error scrolling to bottom:', err);
    }
  }
  
  getUser: GetUserResponse | null = null;
  sender: string = this.getUser?.username || '';
  imageUrlSender: string | null = null


    recipientUser: GetUserResponse | null = null
    imageUrlRecipient: string | null = null


  loadRecipient(): void {
  
      this.authService.getUserByUsername(this.recipient).subscribe(
        (data: GetUserResponse) => {
          this.authService.getProfileImage(data.imageProfile).subscribe(
            (blob: Blob) => {
              const objectUrl = URL.createObjectURL(blob);
              this.imageUrlRecipient = objectUrl;
              this.cdr.detectChanges();
            },
            (error) => {
              console.error("Error loading profile image:", error);
            }
          );
        },
        (error) => {
          console.error("Error loading user by username:", error);
        }
      );
  }
  

  loadUser(): void {
    this.authService.getUserById().subscribe(
      (data: GetUserResponse) => {

      //  this.getUser = data;

        this.sender = data.username
        if (data.imageProfile ){
          this.authService.getProfileImage(data.imageProfile).subscribe(
            (blob : Blob) => {
              const objectUrl  = URL.createObjectURL(blob)
              this.imageUrlSender = objectUrl
              this.cdr.detectChanges()
            }
          )
        }
        
       // this.imageUrlSender = data.imageProfile
      console.log(`Initializing connection for user ${this.sender}...`);
       this.webSocketService.connect(this.sender);
       if (this.recipient && !this.isSubscribed) {
        setTimeout(() => {
          console.log("Subscribing to messages after connection...");
          this.subscriptionToMessages();
        }, 2000);
      } else {
        console.log('No recipient specified or already subscribed');
      }
      }
    )
  }

  

  private isSubscribed: boolean = false;
  subscriptionToMessages() {
    if (this.isSubscribed) {
      console.log("Already subscribed to messages.");
      return;
    }
    console.log("Subscribing to messages for user:", this.sender);
    this.subscription = this.webSocketService.subscribeToMessages(this.sender).subscribe(
      (message: Message) => {
        if (
          (message.sender === this.sender && message.recipient === this.recipient) ||
          (message.sender === this.recipient && message.recipient === this.sender)
        ) {
          const messageExists = this.messages.some(
            (msg) => msg.timestamp === message.timestamp && msg.sender === message.sender
          )
          if (!messageExists) {
            console.log("New message received:", message);
            this.ngZone.run(() => {


              message.mediaBlob = [];

              if (message.mediaUrls && message.mediaUrls.length > 0) {
                console.log("Downloading media for message:", message.mediaUrls);
  
                message.mediaUrls.forEach((mediaUrl, index) => {
                  this.authService.getMediaUrls(mediaUrl).subscribe(
                    (blob: Blob) => {
                      console.log(`Media ${index + 1} downloaded:`, blob);
                      const objectURL = URL.createObjectURL(blob);
                      message.mediaBlob!.push(objectURL);
  
                      this.cdr.detectChanges();
                      console.log("Updated mediaBlobs:", message.mediaBlob);
                    },
                    (error) => {
                      console.error(`Error downloading media ${index + 1}:`, error);
                    }
                  );
                });
              } else {
                console.log("No media found for this message.");
              }

              this.messages.push(message);
              console.log('Messages array after push:', this.messages);
              this.scrollToBottom()
            });
          }
        }
      },
      (error) => {
        console.error("Error in subscription:", error);
      }
    );
    this.isSubscribed = true;
  }

  selectedFile: File[] = []
  mediaUrls: string[] = []

  sendMessage(): void {
    if (this.content.trim() !== '' || this.selectedFile.length > 0) {
      
      let messageType = '';
      if (this.selectedFile.length > 0) {
        const fileTypes = this.selectedFile.map(file => this.isImage(file.name) ? 'IMAGE' : (this.isVideo(file.name) ? 'VIDEO' : ''));
        if (fileTypes.includes('VIDEO') && fileTypes.includes('IMAGE')) {
          messageType = 'MIXED_MEDIA';
        } else if (fileTypes.includes('VIDEO')) {
          messageType = 'VIDEO';
        } else if (fileTypes.includes('IMAGE')) {
          messageType = 'IMAGE';
        } else {
          messageType = 'TEXT';
        }
      } else if (this.content.trim() !== '') {
        messageType = 'TEXT';
      }

      const newMessage: Message = {
        sender: this.sender,
        recipient: this.recipient,
        content: this.content,
        timestamp: new Date().toISOString(),
        imageSender: this.imageUrlSender ?? '',
        imageRecipient: this.imageUrlRecipient ?? '',
        type: messageType,
        mediaUrls: [],
        mediaBlob: [] 
      };
  
      this.messages.push(newMessage);
      this.cdr.detectChanges();
      this.scrollToBottom();
      
      
      if (this.selectedFile.length > 0) {
        this.authService.uploadMediaUrlsInChat(this.selectedFile).subscribe(
          (uploadedUrls: string[]) => {
            console.log('Upload success:', uploadedUrls);

            newMessage.mediaUrls = uploadedUrls
            
            this.webSocketService.sendMessage(
              this.sender, this.recipient, this.content, 
              this.imageUrlSender ?? '', this.imageUrlRecipient ?? '', uploadedUrls
            );
  
            uploadedUrls.forEach((mediaUrl) => {
              this.authService.getMediaUrls(mediaUrl).subscribe(
                (blob: Blob) => {
                  const objectURL = URL.createObjectURL(blob);
                  newMessage.mediaBlob!.push(objectURL);
                  this.cdr.detectChanges();
                  this.scrollToBottom()
                },
                (error) => console.error("Media error:", error)
              );
            });
            this.updateMessageUI(newMessage);
            this.content = '';
            this.selectedFile = [];
            this.mediaUrls = [];
            this.cdr.detectChanges();
            this.scrollToBottom();

          },
          error => console.error('Error upload file:', error)
        );
      } else {
        this.webSocketService.sendMessage(
          this.sender, this.recipient, this.content,
          this.imageUrlSender ?? '', this.imageUrlRecipient ?? '', []
        );
  
        /*this.messages.push({
          sender: this.sender,
          recipient: this.recipient,
          content: this.content,
          timestamp: new Date().toISOString(),
          imageSender: this.imageUrlSender,
          imageRecipient: this.imageUrlRecipient,
          type: '',
          mediaUrls: this.mediaUrls
        });
  */

        this.updateMessageUI(newMessage);
        this.content = '';
        this.selectedFile = [];
        this.mediaUrls = [];
        this.cdr.detectChanges();
        this.scrollToBottom();
      }
    }
  }
  
  updateMessageUI(newMessage: Message) {
    const index = this.messages.findIndex(msg => msg.timestamp === newMessage.timestamp);
    if (index !== -1) {
      this.messages[index] = newMessage; 
    }
    this.cdr.detectChanges();
    this.scrollToBottom();
  }

  handleFileInput(event: any) {
    const files: FileList = event.target.files;
    if (files) {
      for (let i= 0; i<files.length;i++) {
        this.selectedFile.push(files[i])
      }
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.selectedFile = Array.from(input.files);
    }
  }

  
  openFileMediaUrls(fileInputClick: HTMLInputElement) {
    fileInputClick.click()
  }

  isImage(url: string): boolean {
    return url.match(/\.(jpeg|jpg|png|gif|bmp|tiff|webp)$/i) !== null;
  }
  
  isVideo(url: string): boolean {
    return url.match(/\.(mp4|mov|avi|mkv|webm)$/i) !== null;
  }

  loadMedia(mediaId: string): void {
    this.authService.getMediaUrls(mediaId).subscribe(
      (blob: Blob) => {
        const objectUrl = URL.createObjectURL(blob);
        this.convertedMediaUrls[mediaId] = objectUrl;
        console.log("Converted URL for", mediaId, ":", objectUrl);
        this.cdr.detectChanges();
      },
      error => {
        console.error("Error loading media", mediaId, error);
      }
    );
  }
  
  
  convertedMediaUrls: { [mediaId: string]: string } = {};

  

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  

  loadChatHistory(): void {
    this.authService.getUserById().subscribe((userData) => {
      if (this.sender && this.recipient) {
        this.authService.getChatHistory(this.sender, this.recipient).subscribe(
          (message: Message[]) => {
            this.messages = message;

            this.messages.forEach((msg) => {
              if (msg.mediaUrls) {

                msg.mediaBlob = [];
                msg.mediaUrls.forEach(mediaUrl => {
                  this.authService.getMediaUrls(mediaUrl).subscribe(
                    (blob: Blob) => {
                      const objectUrl = URL.createObjectURL(blob);
                      msg.mediaBlob!.push(objectUrl);
                      this.cdr.detectChanges()
                      this.scrollToBottom()
                    },
                    (error) => {
                      console.error("Nedia error: ", error)
                    }
                  )
                })
              }
            })

            this.cdr.detectChanges();
            this.scrollToBottom()
          });
      }
    })
    
  }
  
  

  getGroupedMessages() {
    const groupedMessages: {
      [key: string] : Message[]
    } = {}

    const today = new Date();
    today.setHours(0,0,0,0)

    this.messages.forEach((msg) => {

      const messageDate = new Date(msg.timestamp);
      messageDate.setHours(0,0,0,0)
      const timeDiff = today.getTime() - messageDate.getTime()
      const dayDiff = Math.floor(timeDiff / (1000 * 60 * 60 *24))

      let dateKey: string

      if (dayDiff === 0) {
        dateKey = 'Today'
      } else if (dayDiff === 1) {
        dateKey = 'Yesterday'
      } else if (dayDiff < 7) {
        dateKey = messageDate.toLocaleDateString('en-US', {
          weekday: 'long'
        })
      } else {
        dateKey = messageDate.toLocaleDateString('en-US', {
          month: 'short',
          day: 'numeric',
          year: 'numeric'
        })
      }
          
      if (!groupedMessages[dateKey]) {
        groupedMessages[dateKey] = [];
      }

      groupedMessages[dateKey].push(msg);

    })

    return Object.entries(groupedMessages);
  }


  handleKeydown(event: KeyboardEvent) {
    if (event.key === 'Shift') {
      event.preventDefault();
      this.content += '\n'
    }
    if (event.key === 'Enter') {
      event.preventDefault();
      this.sendMessage()
    }
    if (event.ctrlKey && event.key === 'e') {
      event.preventDefault()
      this.toggleEmojiPicker()
    }
  }

  showEmojiPicker: boolean = false
  toggleEmojiPicker(){
    this.showEmojiPicker = !this.showEmojiPicker


    if (this.showEmojiPicker) {
      setTimeout(() => {
        new Picker({
          onEmojiSelect: (emoji: any) => this.addEmoji(emoji),
          parent: document.querySelector('#emoji-container')
        })
      }, 0)
    }

  }
  addEmoji(emoji: any) {
    this.content += emoji.native;
    this.cdr.detectChanges()
  }
}