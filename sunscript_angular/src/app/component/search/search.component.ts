import { Component } from '@angular/core';
import { AuthService, GetSearchUserResponse } from '../../authentication/auth.service';
import { NgIf, NgFor } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [NgIf, NgFor, ReactiveFormsModule, FormsModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent {

  constructor(private authService: AuthService,
    private router: Router
  ) {}
  
    ngOnInit(): void {
        //this.getUserProfile()
    }
    
  
    query: string = ''
    searchUser: GetSearchUserResponse[] = []
    profileImagesUrl: Record<string, string> = {}
    backgroundImagesUrl: Record<string, string> = {}
  
    onSearchUser(): void {
      if (this.query.length >= 2) {
        this.authService.getSearchUser(this.query).subscribe(
          (data) => {
            this.searchUser = data
            console.log(this.searchUser)

            this.searchUser.forEach(user => {
              if (user.imageProfile) {
                this.authService.getProfileImage(user.imageProfile).subscribe(
                  (data: Blob) => {
                    const imageUrl = URL.createObjectURL(data);
                    this.profileImagesUrl[user.discriminator] = imageUrl
                  }
                );
              }

              if (user.imageBackground) {
                this.authService.getProfileImage(user.imageBackground).subscribe(
                  (data: Blob) => {
                    const imageUrl = URL.createObjectURL(data);
                    this.backgroundImagesUrl[user.discriminator] = imageUrl
                  }
                );
              }

            })

          },
          (error) => {
            console.log("Error fetching search: ", error)
          }
        )
      } else {
        this.searchUser = []
        console.log("Null")
      }
    }
  
    matchText(text: string) : boolean {
      if (this.query && text) {
        return text.toLowerCase().includes(this.query.toLowerCase())
      }
      return false
    }

  navigationToUser(discriminator: string): void {
    this.router.navigate(["/sunscript/user"], {queryParams: {p: discriminator}})
  }
}
