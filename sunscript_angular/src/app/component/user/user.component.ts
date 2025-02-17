import { Component, OnInit } from '@angular/core';
import { AuthService, GetUserResponse } from '../../authentication/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { count } from 'console';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [NgIf],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit{

  constructor (
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  discriminator: string | null = null
  user: GetUserResponse | null = null
  profileImageUrl: string | null = null
  backgroundImageUrl: string | null = null

  isFollowed: boolean = false
  isFollowBack: boolean = false

  countFollowers: number = 0;
  countFollowing: number = 0;




  ngOnInit(): void {
      this.route.queryParams.subscribe(
        params => {
          this.discriminator = params['p']
          if (this.discriminator) {
            window.scrollTo(0, 0)
            this.loadUser(this.discriminator)
        }
      }
    )
  }


  loadUser(discriminator: string) {
    this.authService.getOtherUserByDiscriminator(discriminator).subscribe(
      (response: GetUserResponse) => {
        this.user = response
        if (this.user?.imageProfile) {
          this.authService.getProfileImage(this.user.imageProfile).subscribe(
            (data: Blob) => {
              this.profileImageUrl = URL.createObjectURL(data)
            }
          )
        }
        
        if (this.user?.imageBackground) {
          this.authService.getBackgroundImage(this.user.imageBackground).subscribe(
            (data: Blob) => {
              this.backgroundImageUrl = URL.createObjectURL(data)
            }
          )
        }

        this.checkFollowUser()
        this.checkFollowBackUser()
        this.loadCoutFollowers()
        this.loadCoutFollowing()

        

      },  
      error => {
        console.error('Error fetching user', error);
      }
    )
  }

  loadCoutFollowers() {
    if (this.user?.id) {
      this.authService.getCountFollowers(this.user.id).subscribe(response => {
        this.countFollowers = response.countFollowers
      })
    }
  }

  loadCoutFollowing() {
    if (this.user?.id) {
      this.authService.getCountFollowing(this.user.id).subscribe(response => {
        this.countFollowing = response.countFollowing
      })
    }
  }


  btnUserFollow(): void {
    if (this.user?.id) {
      const following = this.user.id;
      this.authService.getUserFollow(following).subscribe(
        response => {
         // if (response && response.message) {
           // console.log(response);
          //} else {
            //console.log("Follow successful");
            this.isFollowed = true
            this.checkFollowUser()
            this.checkFollowBackUser()
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

  btnUserUnfollow(): void {
    if (this.user?.id) {
      const following = this.user.id; 
      this.authService.getUserUnfollow(following).subscribe(
        response => {
          //if (response && response.message) {
            //console.log(response);
          //} else {
            //console.log("Unfollow successful");
            this.isFollowed = false
            this.checkFollowUser()
            this.checkFollowBackUser()
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


  checkFollowUser(): void {
    if (this.user?.id) {
      this.authService.checkFollow(this.user.id).subscribe(
        (response) => {
          this.isFollowed = response.isFollowed;
          this.loadCoutFollowers()
          this.loadCoutFollowing()
        },
        (error) => {
          console.error('Error checking follow status', error);
        }
      );
    }
  }

  checkFollowBackUser(): void {
    if (this.user?.id) {
      this.authService.checkFollowBack(this.user.id).subscribe(
        (response) => {
          this.isFollowBack = response.isFollowBack;
          this.loadCoutFollowers()
          this.loadCoutFollowing()
        },
        (error) => {
          console.error('Error checking follow status', error);
        }
      );
    }
  }

  navigationToChat(username: string): void {
    this.router.navigate(['/sunscript/chat'],{queryParams: {u: username}})
  }
  

}
