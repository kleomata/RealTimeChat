import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms'
import { NgFor, NgIf, NgClass } from '@angular/common';
import { AuthService } from '../../authentication/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register-user',
  standalone: true,
  imports: [FormsModule, NgFor, ReactiveFormsModule, RouterLink, NgIf, NgClass],
  templateUrl: './register-user.component.html',
  styleUrl: './register-user.component.css'
})
export class RegisterUserComponent {

  formFields = [
    { label: "First Name", placeholder: "Johan", formControlName: "firstName", type: "text", for: "firstName", id: "firstName" },
    { label: "Last Name", placeholder: "Doe", formControlName: "lastName", type: "text", for: "lastName", id: "lastName" },
    { label: "Birthday", placeholder: "YYYY-MM-DD", formControlName: "birthday", type: "date", for: "birthday", id: "birthday" },
    { label: "Country", placeholder: "USA", formControlName: "country", type: "text", for: "country", id: "country" },
    { label: "City", placeholder: "New York", formControlName: "city", type: "text", for: "city", id: "city" },
    { label: "Address", placeholder: "123 Street", formControlName: "address", type: "text", for: "address", id: "address" },
    { label: "Gender", placeholder: "Male/Female", formControlName: "gender", type: "text", for: "gender", id: "gender" },
    { label: "Phone", placeholder: "+1234567890", formControlName: "phone", type: "text", for: "phone", id: "phone" },
  ]
  formFieldsCredentials = [
    { label: "Username", placeholder: "johndoe", formControlName: "username", type: "text", for: "username", id: "username" },
    { label: "Email", placeholder: "johndoe@example.com", formControlName: "email", type: "email", for: "email", id: "email" },
    { label: "Password", placeholder: "********", formControlName: "password", type: "password", for: "password", id: "password" },
    { label: "Confirm Password", placeholder: "********", formControlName: "passwordConfirm", type: "password", for: "passwordConfirm", id: "passwordConfirm" },
  ]

  currentStep: number = 1;
  register: FormGroup;
  isSubmitted: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.register = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      birthday: ['', [Validators.required]],
      country: ['', [Validators.required]],
      city: ['', [Validators.required]],
      address: ['', [Validators.required]],
      gender: ['', [Validators.required]],
      phone: ['', [Validators.required]],
      bio: ['', [Validators.required]],
      username: ['', [Validators.required, Validators.minLength(4)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      passwordConfirm: ['', [Validators.required]]
    }, {validator: this.passwordMatchPasswordConfirmValidator})
  }

  private passwordMatchPasswordConfirmValidator(formGroup: FormGroup): void {
    const password = formGroup.get('password')
    const passwordConfirm = formGroup.get('passwordConfirm')

    if (password?.value !== passwordConfirm?.value) {
      passwordConfirm?.setErrors({passwordMismatch: null})
    } else {
      passwordConfirm?.setErrors(null)
    }

  }

  selectedImageProfile: File | null = null
  imageProfilePreview: String | null = null

  onFileIMageProfile(event: any) {
    const file: File = event.target.files[0]
    if (file) {
      this.selectedImageProfile = file
      this.register.patchValue({imageProfile: file})
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imageProfilePreview = e.target.result
      }
      reader.readAsDataURL(file)
    }
  }
  removeImageProfile() {
    this.imageProfilePreview = null;
    this.selectedImageProfile = null;
    this.register.patchValue({imageProfile: null})
  }
  public isImageProfileSelected(): boolean {
    return !!this.selectedImageProfile
  }

  selectedImageBackground: File | null = null
  imageBackgroundPreview: String | null = null

  onFileImageBackground(event: any) {
    const file = File = event.target.files[0]
    if (file) {
      this.selectedImageBackground = file
      this.register.patchValue({imageBackground: file})
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imageBackgroundPreview = e.target.result
      }
      reader.readAsDataURL(file) 
    }
  }
  removeImageBackground() {
    this.imageBackgroundPreview = null;
    this.selectedImageBackground = null;
    this.register.patchValue({imageBackground: null})
  }
  public isImageBackgroundSelected(): boolean {
    return !!this.selectedImageBackground;
  }


  // Buttons
  nextStep(): void {
    this.isSubmitted = true

    if (this.isStepValid()) {
      if (this.currentStep < 4) {
        this.currentStep++;
        this.isSubmitted = false
        console.log('click')
      }
    }
  }

  prevStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
      this.isSubmitted = false
    }
  }

  submitForm(): void {
    const registerUser: any = {
      ...this.register.value
    }


    if(this.selectedImageProfile && this.selectedImageBackground) {
      this.authService.registerUser(registerUser, this.selectedImageProfile, this.selectedImageBackground).subscribe({
        next: (response) => {
          console.log("User added successfully!", response);
          console.log(registerUser)
          //this.register.reset();
          //this.currentStep = 1;
          this.register.reset();
          this.selectedImageBackground = null
          this.imageBackgroundPreview = null
          this.selectedImageProfile = null
          this.imageProfilePreview = null
          this.currentStep = 1
        },
        error: (error) => {
          console.error("Error adding user: ", error);
        }
      })
    } else {
      console.error("No picture selected.");
    }
  }
 
  // Steps

  isStepValid(): boolean {
    switch (this.currentStep) {
      case 1: 
        return this.isFormValid(['firstName', 'lastName', 'birthday', 'country', 'city', 'address', 'gender','phone'])
      case 2:
        return this.isImageProfileSelected() && this.isImageBackgroundSelected();
      case 3: 
        return this.isFormValid(['bio'])
      case 4: 
        return this.isFormValid(['username', 'email', 'password', 'passwordConfirm'])
      default:
        return false
    }
  }

  private isFormValid(fields: string[]): boolean {
    return fields.every(field => this.register.get(field)?.valid)
  }

  isStepCompleted(step: number): boolean {
    return this.currentStep > step
  }

  isStepCurrent(step: number): boolean {
    return this.currentStep === step;
  }

  /////

  navigationToLogin(): void {
    this.router.navigate(['/sunscript/login'])
  }


}
