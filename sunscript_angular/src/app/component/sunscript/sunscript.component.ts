import { ChangeDetectorRef, Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { AllFollowingUserResponse, AuthService, GetUserResponse } from '../../authentication/auth.service';
import { WebsocketService } from '../../authentication/websocket.service';
import { Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-sunscript',
  standalone: true,
  imports: [RouterOutlet, NgFor, RouterLink, NgIf, FormsModule, NgClass],
  templateUrl: './sunscript.component.html',
  styleUrl: './sunscript.component.css'
})
export class SunscriptComponent implements OnInit, OnDestroy{

  constructor (
    private authService: AuthService,
    private router: Router,
    private wevSocketService: WebsocketService,
  ) {}

  isOnline: boolean = false
  private userStatusSubscription: Subscription | undefined;
  otherUserStatus: Record<string, boolean> = {}


  getUser: GetUserResponse | null = null
    sender: string = this.getUser?.username || ''
    loadUser(): void {
      this.authService.getUserById().subscribe(
        (data: GetUserResponse) => {
          this.getUser = data;
    
          this.sender = data.username
          console.log(`Initializing connection for user ${this.sender}...`);
          this.wevSocketService.connect(this.sender);
        }
      )
    }
  profileImagesUrl: Record<string, string> = {}
  backgroundImagesUrl: Record<string, string> = {}

  ngOnInit(): void {
    this.loadUser();
    this.loadAllFollowing()

    this.userStatusSubscription = this.wevSocketService.getUseStatus().subscribe(status => {
      this.otherUserStatus = status

      this.followingList.forEach(user => {
        if (status[user.username] !== undefined) {
          user.online = status[user.username]
        }
      })
    })


    document.addEventListener('visibilitychange', this.handleVisibilityChange.bind(this));
  }

  ngOnDestroy() {
    console.log(`Disconnecting user ${this.sender}...`);
    document.removeEventListener('visibilitychange', this.handleVisibilityChange.bind(this));
    if(this.userStatusSubscription) {
      this.userStatusSubscription.unsubscribe();
    }
    this.wevSocketService.disconnect(this.sender);

  }

  @HostListener('window:beforeunload', ['$event'])
  @HostListener('window:unload', ['$event'])
  handleUnload(event: Event): void {
    console.log('User is leaving the page or closing the browser.');
    if (this.sender) {
      this.wevSocketService.setUserOffline(this.sender);
    }
  }

  handleVisibilityChange(): void {
    if (document.hidden) {
      console.log('User is leaving the page or switching to another tab.');
      if (this.sender) {
        this.wevSocketService.setUserOffline(this.sender);
      }
    } else {
      console.log('User is back on the page.');
      if (this.sender) {
        this.wevSocketService.setUserOnline(this.sender);
      }
    }
  }

  followingList: (AllFollowingUserResponse & {
    isFollowed: boolean;
    isFollowBack: boolean;
  })[] = [];

  loadAllFollowing(): void {
    this.authService.getAllFollowing().subscribe(
      (data: AllFollowingUserResponse[]) => {
        this.followingList = data.map(following => ({
          ...following,
          isFollowed: false,
          isFollowBack: false,
          online: !!following.online
        }))

        this.followingList.forEach(following => {

          console.log(following)
          console.log("isOnlineUser value:", following.online);

          if (following.imageProfile) {
            this.authService.getProfileImage(following.imageProfile).subscribe(
              (data: Blob) => {
                const imageUrl = URL.createObjectURL(data)
                this.profileImagesUrl[following.discriminator] = imageUrl
              }
            )
          }

          if (following.imageBackground) {
            this.authService.getProfileImage(following.imageBackground).subscribe(
              (data: Blob) => {
                const imageUrl = URL.createObjectURL(data)
                this.backgroundImagesUrl[following.discriminator] = imageUrl
              }
            )
          }

          this.checkFollowUser(following)
          this.checkFollowBackUser(following)

        })
        
      },
      error => {
        console.error('Error fetching following users', error);
      }
    )
  }

  

  sideNavBar = [
    {
      link: '/sunscript/home',
      label: 'Home',
      icon: 'fa-solid fa-house'
    },
    {
      link: '/sunscript/profile',
      label: 'Profile',
      icon: 'fa-solid fa-user'
    },
    {
      link: '/sunscript/search',
      label: 'Search',
      icon: 'fa-solid fa-magnifying-glass'
    },
    {
      link: '/sunscript/chat',
      label: 'Chat',
      icon: 'fa-solid fa-message'
    }
  ]

  /* =================== */
  navigate: string = ''

  navigationSearch(event: KeyboardEvent) {
    if (event.key === 'Enter' && this.navigate.trim()) {
      const path = this.navigate.toLowerCase()
      
      if (path.startsWith('chat')) {
        const username = path.replace('chat', '').trim();
        if (username) {
          this.router.navigate(['/sunscript/chat'], {queryParams: {u: username}})
        } else {
          console.log("Username path not found")
        }
      } else {
        const fullPath = `sunscript/${path}`

        const routeExists = this.router.config.some(route => {
          if (route.children) {
            return route.children.some(child => child.path === path)
          }
          return false
        })

        if (routeExists) {
          this.router.navigate([fullPath])
        } else {
          console.log("Path not found")
        }
      } 
    }
  }

  navigationToHome(): void{
    this.router.navigate(["/sunscript/home"])
  }


  navigationToProfile(discriminator: string): void {
    this.router.navigate(["/sunscript/user"], {queryParams: {p: discriminator}})
  }


  // Follow btn 
  countFollowers: number = 0;
  countFollowing: number = 0;

  btnUserFollow(user: AllFollowingUserResponse & {isFollowed: boolean, isFollowBack: boolean}): void {
    if (user.id) {
      this.authService.getUserFollow(user.id).subscribe(
        response => {
         // if (response && response.message) {
           // console.log(response);
          //} else {
            //console.log("Follow successful");
            user.isFollowed = true
            this.checkFollowUser(user)
            this.checkFollowBackUser(user)
          //}
        },
        error => {
          console.error('Error following user', error);
          if (error.status === 404) {
            console.error('User not found or invalid following ID');
          } else {
            console.error('An unexpected error occurred', error);
          } 
        }
      );
    } else {
      console.log('You must be logged in to follow!');
    }
  }

  btnUserUnfollow(user: AllFollowingUserResponse & {isFollowed: boolean, isFollowBack: boolean}): void {
    if (user.id) {
      this.authService.getUserUnfollow(user.id).subscribe(
        response => {
          //if (response && response.message) {
            //console.log(response);
          //} else {
            //console.log("Unfollow successful");
            user.isFollowed = false
            this.checkFollowUser(user)
            this.checkFollowBackUser(user)
            this.loadAllFollowing()
          //}
        },
        error => {
          console.error('Error unfollowing user', error);
          if (error.status === 404) {
            console.error('User not found or invalid unfollowing ID');
          } else {
            console.error('An unexpected error occurred', error);
          } 
        }
      );
    } else {
      console.log('You must be logged in to follow!');
    }
  }


  checkFollowUser(following: AllFollowingUserResponse & {isFollowed: boolean}): void {
    if (following.id) {
      this.authService.checkFollow(following.id).subscribe(
        (response) => {
          following.isFollowed = response.isFollowed;
          //this.loadCoutFollowers()
          //this.loadCoutFollowing()
        },
        (error) => {
          console.error('Error checking follow status', error);
        }
      );
    }
  }

  checkFollowBackUser(following: AllFollowingUserResponse & {isFollowed: boolean, isFollowBack: boolean}): void {
    if (following.id) {
      this.authService.checkFollowBack(following.id).subscribe(
        (response) => {
          following.isFollowBack = response.isFollowBack;
          //this.loadCoutFollowers()
          //this.loadCoutFollowing()
        },
        (error) => {
          console.error('Error checking follow status', error);
        }
      );
    }
  }



  /* ========================= */
  showBoxChat: boolean = false

  loadShowBoxChat() {
    this.showBoxChat = !this.showBoxChat
  }


}
