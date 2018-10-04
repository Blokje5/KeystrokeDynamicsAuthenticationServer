import { KeystrokeDynamicsService } from './keystroke-dynamics/keystroke-dynamics.service';
import { AuthServiceService } from './auth/auth-service.service';
import { BrowserModule } from '@angular/platform-browser';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button';
import {MatStepperModule} from '@angular/material/stepper';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { HttpClientModule } from '@angular/common/http';
import { DataCollectionService } from './data/data-collection.service';
import { OnboardingComponent } from './onboarding/onboarding.component';
import { EnrollmentComponent } from './enrollment/enrollment.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegistrationComponent,
    OnboardingComponent,
    EnrollmentComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    BrowserAnimationsModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatStepperModule
  ],
  providers: [
    AuthServiceService,
    KeystrokeDynamicsService,
    DataCollectionService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
