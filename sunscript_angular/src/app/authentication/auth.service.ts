import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { error } from 'console';
import { response } from 'express';
import { url } from 'inspector';
import { catchError, Observable, tap, throwError } from 'rxjs';

//////////////////////////////////////////////////
// Interface
export interface GetSearchUserResponse {
  firstName: string;
  lastName: string;
  discriminator: string;
  imageProfile: string | null;
  imageBackground: string | null;
  id: string
}

export interface GetUserResponse {
  id: string;
  firstName: string;
  lastName: string;
  birthday: string;
  country: string;
  city: string;
  address: string;
  gender: string;
  phone: string;
  discriminator: string;
  username: string;
  email: string;
  bio: string;
  imageProfile: string;
  imageBackground: string
}

export interface AllFollowingUserResponse {
  id: string;
  firstName: string;
  lastName: string;
  discriminator: string;
  imageProfile: string;
  imageBackground: string;
  countFollowing: number;
  countFollowers: number;
  username: string,
  online: boolean
}

export interface UserStatusResponse {
  online: boolean;
  lastOnlineTime: string
}


//////////////////////////////



@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private http: HttpClient
  ) { }

  // API
  //-> api admin
  private Api_admin = 'http://localhost:8080/api/chat/admin'
  //-> api user
  private Api_user = 'http://localhost:8080/api/chat/user'

  //////////////////////

  /////// Token Config ///////
  private tokenKey = 'token'

  saveToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): String | null {
    return localStorage.getItem(this.tokenKey)
  }

  clearToken() {
    localStorage.removeItem(this.tokenKey);
  }

  //////////////////////////////////
  // Register User
  registerUser(request: any, imageProfile: File, imageBackground: File): Observable<any> {
    const formData = new FormData()
    
    for (const key in request) {
      if (request.hasOwnProperty(key)) {
        const value = request[key as keyof any];
        
        if (Array.isArray(value)) {
          value.forEach(item => formData.append(key, item))
        } else {
          formData.append(key, String(value))
        }
      }
    }
    
    if (imageProfile) {
      formData.append('imageProfile', imageProfile, imageProfile.name)
    }

    if (imageBackground) {
      formData.append('imageBackground', imageBackground, imageBackground.name)
    }

    return this.http.post<any>(`${this.Api_user}/register`, formData)
      .pipe(
        tap((response) => {
          console.log("Register Successfull");
        }),
        catchError((error) => {
          console.error('Register error', error);
          return throwError('An error occurred: ' + error.message || 'Unknown error');
        })
      )
  }

  // Login User
  loginUser(username: String, password: String): Observable<any> {
    return this.http.post<any>(`${this.Api_user}/login`, {username, password})
    .pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          console.log("Token save: ", response.token);
        }
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Login error: ', error);
        if (error.status === 401) {
          console.error("Invalid username or password")
        }
        return throwError(error)
      })
    )
  }


  // Search User
  getSearchUser(params: string): Observable<GetSearchUserResponse[]> {
    const token = localStorage.getItem('token')
    //console.log('TOKEN: ',token)
    //if (token) {
    if (!token) {
      console.log('No token found, please log in');
      return throwError('No token found, please log in');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
      //'Content-Type': 'application/json'
    });
      
    return this.http.get<GetSearchUserResponse[]>(`${this.Api_user}/search/${params}`, {headers})
    
    //} else {
      //return throwError('No token found, please log in');
    //}
  }

  // Get User
  getUserById(): Observable<GetUserResponse> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    return this.http.get<GetUserResponse>(`${this.Api_user}/profile`, {headers})
    .pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Error fetching user profile:', error);
        return throwError(error);
      })
    )
  }
  //Get Image Profile
  getProfileImage(imageProfile: string): Observable<Blob> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    return this.http.get(`${this.Api_user}/profile/image/${imageProfile}`, { responseType: 'blob', headers });

  }
  //Get Image Background
  getBackgroundImage(imageBackground: string): Observable<Blob> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    return this.http.get(`${this.Api_user}/background/image/${imageBackground}`, { responseType: 'blob', headers });

  }

  // Get user 
  getOtherUserByDiscriminator(discriminator: string): Observable<GetUserResponse> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    return this.http.get<GetUserResponse>(`${this.Api_user}/discriminator/${discriminator}`, { headers });
  }

  getUserByUsername(username: string): Observable<GetUserResponse> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    return this.http.get<GetUserResponse>(`${this.Api_user}/userInfo/${username}`, { headers });
  }


  // Follow User Function
  getUserFollow(followingId: string): Observable<any> {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });

    const body = { followingId };
    
    return this.http.post<any>(`${this.Api_user}/follow`, body, { headers, responseType: 'json' });
  }

  checkFollow(userId: string): Observable<{ isFollowed: boolean }> {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
  
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });
  
    return this.http.get<{ isFollowed: boolean }>(`${this.Api_user}/checkFollow/${userId}`, { headers })
      .pipe(
        catchError(error => {
          console.error('Error occurred while checking follow. User ID and is hidden for security reasons.');
          if (error.status === 401) {
            return throwError('Token expired or invalid. Please log in again.');
          }  
          return throwError('An error occurred while checking the subscription. Please try again later.');
        })
      );
  }

  checkFollowBack(userId: string): Observable<{ isFollowBack: boolean }> {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
  
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });
  
    return this.http.get<{ isFollowBack: boolean }>(`${this.Api_user}/checkFollowBack/${userId}`, { headers })
      .pipe(
        catchError(error => {
          console.error('Error occurred while checking follow back. User ID and is hidden for security reasons.');
          if (error.status === 401) {
            return throwError('Token expired or invalid. Please log in again.');
          }  
          return throwError('An error occurred while checking the subscription. Please try again later.');
        })
      );
  }
  
  

  getUserUnfollow(followingId: string): Observable<any> {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
  
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
  
    const body = { followingId };
  
    return this.http.delete<any>(`${this.Api_user}/unfollow`, { headers, body })
      .pipe(
        catchError(err => {
          console.error('Error:', err); 
          return throwError(err);
        })
      );
  }
  
  

  getCountFollowers(userId: string): Observable<{countFollowers: number}> {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });
    
    return this.http.get<{countFollowers: number}>(`${this.Api_user}/countFollowers/${userId}`, {headers});
  }

  getCountFollowing(userId: string): Observable<{countFollowing: number}> {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });
    
    return this.http.get<{countFollowing: number}>(`${this.Api_user}/countFollowing/${userId}`, {headers});
  }


  getAllFollowing(): Observable<AllFollowingUserResponse[]> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    return this.http.get<AllFollowingUserResponse[]>(`${this.Api_user}/allFollowing`, {headers})
    .pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Error fetching user profile:', error);
        return throwError(error);
      })
    )
  }


  /////////////////////////////////////////////
  getUserStatus(username: string): Observable<boolean> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    return this.http.get<boolean>(`/api/chat/user/status/${username}`, { headers, responseType: 'text' as 'json' })    .pipe(
      catchError(error => {
        console.error('Error fetching user status:', error); 
        return throwError('Error fetching user status');
      })
    );
  }


  getChatHistory(sender: string, recipient: string): Observable<any> {
    const token = localStorage.getItem('token');
    if (!token) {
      return throwError(() => new Error('No token found. Please log in.'));
    }
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    return this.http.get<any>(
      `http://localhost:8080/chat/messages/${sender}/${recipient}`,
      { headers }
    );
  }

  getRecipientStatus(username: string): Observable<UserStatusResponse> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    return this.http.get<UserStatusResponse>(`http://localhost:8080/api/chat/user/userStatus/${username}`, { headers })    .pipe(
      catchError(error => {
        console.error('Error fetching user status:', error); 
        return throwError('Error fetching user status');
      })
    );
  }


  uploadMediaUrlsInChat(mediaUrls: File[]): Observable<string[]> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    const formDate = new FormData()
    mediaUrls.forEach(file => formDate.append('mediaUrls', file))

    return this.http.post<string[]>(`http://localhost:8080/api/chat/mediaUrls`,formDate, {headers})
  } 

  getMediaUrls(mediaUrls: string): Observable<Blob> {
    const token = localStorage.getItem('token')
    if (!token) {
      console.error('No token found');
      return throwError('Token not found');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    })

    return this.http.get(`http://localhost:8080/api/chat/mediaUrls/media/${mediaUrls}`, { responseType: 'blob', headers });

  }
  

}
