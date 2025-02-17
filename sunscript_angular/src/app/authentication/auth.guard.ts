import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService)
  const router = inject(Router)

  const isAuthentication = !!authService.getToken();
  if(!isAuthentication) {
    router.navigate(['/login'])
    return false
  }

  return true

};
