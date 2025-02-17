import { Component } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../authentication/auth.service';
import { error } from 'console';

@Component({
  selector: 'app-login-user',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './login-user.component.html',
  styleUrl: './login-user.component.css'
})

export class LoginUserComponent {

  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) 
  {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    })
  }

  login(): void {
    if (this.loginForm.valid) {
      const {username, password} = this.loginForm.value;

      console.log({username, password})

      this.authService.loginUser(username, password).subscribe(
        response => {
          console.log('Login User Succesful!', response);
          this.router.navigate(['/sunscript']);
          this.authService.saveToken(response.token)
        }, error => {
          console.error('Login Failed', error)
        }
      )

    }
  }

}
