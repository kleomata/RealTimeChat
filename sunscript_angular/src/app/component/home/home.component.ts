import { Component, OnInit } from '@angular/core';
import { AuthService, GetSearchUserResponse, GetUserResponse } from '../../authentication/auth.service';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, NgIf, NgFor],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
      //this.getUserProfile()
  }
  

  query: string = ''
  searchUser: GetSearchUserResponse[] = []

  onSearchUser(): void {
    if (this.query.length >= 2) {
      this.authService.getSearchUser(this.query).subscribe(
        (data) => {
          this.searchUser = data
          console.log(this.searchUser)
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
