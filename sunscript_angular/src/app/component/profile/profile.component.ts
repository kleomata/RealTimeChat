import { Component, OnInit } from '@angular/core';
import { AuthService, GetUserResponse } from '../../authentication/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NgIf],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit{


  constructor (
    private authService: AuthService
  ) {}

  ngOnInit(): void {
      this.getUserProfile();
  }

  profile: GetUserResponse | null = null
  profileImageUrl: string | null = null
  backgroundImageUrl: string | null = null


  getUserProfile(): void {
    this.authService.getUserById().subscribe({
      next: (response) => {
        this.profile = response;

        if (this.profile?.imageProfile) {
          this.authService.getProfileImage(this.profile.imageProfile).subscribe(
            (data: Blob) => {
              this.profileImageUrl = URL.createObjectURL(data)
            }
          )
        }

        if (this.profile?.imageBackground) {
          this.authService.getBackgroundImage(this.profile.imageBackground).subscribe(
            (data: Blob) => {
              this.backgroundImageUrl = URL.createObjectURL(data)
            }
          )
        }



      },
      error: (err) => {
        console.error("Error fetching user data:", err);
      }
    })
  }



}
