import { HttpClient, HttpHeaders } from '@angular/common/http';
import { HostListener, Injectable } from '@angular/core';
import { StompSubscription,IMessage } from '@stomp/stompjs';
import { BehaviorSubject, Observable, Subject, Subscription, throwError } from 'rxjs';
import SockJS from 'sockjs-client';
import { Client, Frame } from '@stomp/stompjs';
import { Stomp } from '@stomp/stompjs';
import { error } from 'console';
export interface Message {
  sender: string;
  recipient: string;
  content: string;
  timestamp: string;
  imageSender: string | null;
  imageRecipient: string | null;
  type: string;
  mediaUrls: string[];
  mediaBlob?: string[]
  //status: string;
}

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
 
  private stompClient: any;
  private token: string | null = null;
  private userStatusSubject: BehaviorSubject<any> = new BehaviorSubject<any>({ online: false });
  private allUsersStatus: Record<string, boolean> = {}

  constructor() {
    this.token = localStorage.getItem('token');
  }

  setToken(token: string) {
    this.token = token;
  }

  private getToken(): string | null {
    return this.token || localStorage.getItem('token');
  }

  connect(username: string) {
    const token = this.getToken(); 

    if (!token) {
      console.error('Token not found. Please log in again.');
      return;
    }

   // this.stompClient = new Client({

   // })

    const webSocketFactory = () => new SockJS(`http://localhost:8080/ws?token=${token}`);

    const headers = {
      Authorization: `Bearer ${token}` 
    };

    this.stompClient = Stomp.over(webSocketFactory);

    this.stompClient.connect(headers, (frame: Frame) => {

      console.log('Connected to server:', frame);

      if (this.stompClient.connected) {
        console.log("STOMP connection established successfully.");
        
        this.stompClient.send('/app/connect', {}, username); 

        console.log(`User ${username} connected.`);

        this.stompClient.subscribe('/topic/userStatus', (message: any) => {
          const status = JSON.parse(message.body);
          console.log(`User ${status.username} status updated: ${status.online ? 'ONLINE' : 'OFFLINE'}`);
          
          //this.userStatusSubject.next(status);
          this.allUsersStatus[status.username] = status.online;  // përditëson statusin për çdo përdorues
          this.userStatusSubject.next(this.allUsersStatus);
        });

        this.subscribeToMessages(username)//.subscribe(message => {
          //console.log("Private message received:", message);
         // const currentMessages = this.messageSubject.value;
          //this.messageSubject.next([...currentMessages, message])
        //});
        //this.subscribeToMessages('alpha');
      } else {
        console.error("STOMP connection was not estabilshed.")
      }

      
    }, (error: any) => {
      console.error("Websocket error: ",error)
    });
  }

  getUseStatus() {
    return this.userStatusSubject.asObservable()
  }

  disconnect(username: string) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.send('/app/disconnect', {}, username);
      console.log(`User ${username} disconnected.`);
      this.stompClient.disconnect();
    }
  }

  setUserOffline(username: string) {
    if (this.allUsersStatus[username]) {
      this.allUsersStatus[username] = false;
      this.userStatusSubject.next(this.allUsersStatus);
      this.stompClient.send('/app/disconnect', {}, username);
      console.log(`${username} marked as offline.`);
    }
  }

  setUserOnline(username: string) {
    if (!this.allUsersStatus[username]) {
      this.allUsersStatus[username] = true;
      this.userStatusSubject.next(this.allUsersStatus); 
      this.stompClient.send('/app/connect', {}, username); 
      console.log(`${username} marked as online.`);
    }
  }
  
  //private messageSubject = new BehaviorSubject<Message[]>([]); 
  //private messageSubject: BehaviorSubject<Message[]> = new BehaviorSubject<Message[]>([]); 
  private messageSubject = new BehaviorSubject<Message[]>([]);
  private messageSubscription: StompSubscription | null = null;
  public messages$ = this.messageSubject.asObservable();

  subscribeToMessages(username: string): Observable<Message> {
    return new Observable((observer) => {
      console.log("Subscribing to messages for recipient:", username);
      const subscribeToQueue = () => {
        if (this.stompClient?.connected) {
          console.log("STOMP Client exists, subscribing now...");
            
          if (this.messageSubscription) {
            this.messageSubscription.unsubscribe();
          }
    
           // const subscription = this.stompClient.subscribe(`/user/${recipient}/queue/messages`, (message: IMessage) => {
          this.messageSubscription = this.stompClient.subscribe(`/user/queue/messages`, (message: IMessage) => {
            console.log("Message received on subscription:", message.body);
            try {
              const parsedMessage = JSON.parse(message.body);
              console.log("Parsed Message:", parsedMessage);
              const currentMessages = this.messageSubject.value;
              if (!currentMessages.some(msg => msg.timestamp === parsedMessage.timestamp && msg.sender === parsedMessage.sender)) {
                this.messageSubject.next([...currentMessages, parsedMessage]);
                observer.next(parsedMessage);
              }
            } catch (err) {
              console.error("Error parsing message:", err);
            }
          });
            
          console.log("Subscribed to the message queue");
            //return () => subscription.unsubscribe();
        }
      };
       // };
  
      if (this.stompClient?.connected) {
        subscribeToQueue();
      } else {
        this.stompClient?.activate();
        setTimeout(() => {
          if (this.stompClient?.connected) {
            subscribeToQueue();
          } else {
            console.error("Failed to connect WebSocket.");
          }
        }, 2000);
      }
    });
  }
  
  sendMessage(sender: string, recipient: string, content: string, imageSender: string, imageRecipient: string, mediaUrls: string[] = []): void {
    const message = { 
      sender, 
      recipient, 
      content, 
      timestamp: new Date().toISOString(), 
      imageSender,
      imageRecipient,
      mediaUrls
    };
    
    if (this.stompClient?.connected) {
      console.log('Sending message:', message);
    
      this.stompClient.publish({
        destination: '/app/sendPrivateMessage',
        body: JSON.stringify(message)
      });
    } else {
      console.error('WebSocket is not connected. Cannot send message.');
      
      this.stompClient?.activate();
      setTimeout(() => {
        if (this.stompClient?.connected) {
          this.stompClient.publish({
            destination: '/app/sendPrivateMessage',
            body: JSON.stringify(message)
          })
        } else {
          console.error('WebSocket still not connected.');
        }
      }, 2000)
    }
  }
    
  getMessages(): Observable<Message[]> {
    return this.messageSubject.asObservable();
  }
}
