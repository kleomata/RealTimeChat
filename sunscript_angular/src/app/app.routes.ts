import { Routes } from '@angular/router';
import path from 'node:path';
import { LoginUserComponent } from './component/login-user/login-user.component';
import { RegisterUserComponent } from './component/register-user/register-user.component';
import { HomeComponent } from './component/home/home.component';
import { authGuard } from './authentication/auth.guard';
import { SunscriptComponent } from './component/sunscript/sunscript.component';
import { ProfileComponent } from './component/profile/profile.component';
import { SearchComponent } from './component/search/search.component';
import { UserComponent } from './component/user/user.component';
import { ChatComponent } from './component/chat/chat.component';

export const routes: Routes = [
    {
        path: '',
        redirectTo: '/sunscript/login',
        pathMatch: 'full',
    },
    {
        path: 'sunscript/login',
        component: LoginUserComponent
    },
    {
        path: 'sunscript/register',
        component: RegisterUserComponent
    },
    {
        path: 'sunscript',
        component: SunscriptComponent,
        canActivate: [authGuard],
        children: [
            {path: '', redirectTo: 'home', pathMatch: 'full'},
            {path: 'home', component: HomeComponent},
            {path: 'profile', component: ProfileComponent},
            {path: 'search', component: SearchComponent},
            {path: 'user', component: UserComponent},
            {path: 'chat', component: ChatComponent}
        ]
    }
];
