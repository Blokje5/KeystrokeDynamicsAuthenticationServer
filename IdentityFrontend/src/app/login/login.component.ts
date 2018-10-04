import { KeystrokeDynamicsService } from './../keystroke-dynamics/keystroke-dynamics.service';
import { AuthServiceService } from '../auth/auth-service.service';
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  @ViewChild('usernameInput') inputEl: ElementRef;

  constructor(
    private auth: AuthServiceService,
    private kds: KeystrokeDynamicsService
  ) { }

  onKeyEvent(event) {
    this.kds.collectEvent(event);
  }

  login() {
    console.log(this.kds.getEventArray());
    this.auth
      .login(this.username, this.password, this.kds.getEventArray())
      .subscribe(
        (res) => {
          this.username = '';
          this.password = '';
          this.inputEl.nativeElement.focus();
          alert('succes');
        },
        (err) => {
          console.log(err);
          this.kds.reset();
          this.username = '';
          this.password = '';
          this.inputEl.nativeElement.focus();
          alert(err.error.message);
        }
      );
  }
}
