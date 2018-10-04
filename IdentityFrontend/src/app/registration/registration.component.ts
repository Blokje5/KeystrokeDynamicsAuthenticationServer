import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { AuthServiceService } from '../auth/auth-service.service';
import { KeystrokeDynamicsService } from '../keystroke-dynamics/keystroke-dynamics.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent {

  username = '';
  password = '';
  confirmPassword = '';

  constructor(
    private auth: AuthServiceService,
    private keystrokeService: KeystrokeDynamicsService,
  ) { }
  
  register() {
    this.auth
      .register(this.username, this.password, this.confirmPassword)
      .subscribe(
        user => {
          alert('success');
          this.keystrokeService.transmitSession(user.username);
        },
        (err) => alert(err.message) // I'm sorry JS developers :(
      );
  }
}
