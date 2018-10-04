import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { KeystrokeDynamicsService } from '../keystroke-dynamics/keystroke-dynamics.service';
import { AuthServiceService } from '../auth/auth-service.service';

@Component({
  selector: 'app-enrollment',
  templateUrl: './enrollment.component.html',
  styleUrls: ['./enrollment.component.css']
})
export class EnrollmentComponent implements OnInit {
  username = '';
  counter = 0;
  @ViewChild('usernameInput') inputEl: ElementRef;

  constructor(private kdsService: KeystrokeDynamicsService, private auth: AuthServiceService) { }

  ngOnInit() {
  }

  onKeyEvent(event) {
    this.kdsService.collectEvent(event);
  }

  validate() {
    if (this.counter < 6) {
      // 6 attempts have been made, should be enough to create a nice sample
      if (this.kdsService.getUsername() !== this.username) {
        console.log(this.username);
        alert(`wrong username, expected ${this.kdsService.getUsername()}`);
        return;
      } else {
        this.kdsService.onSample();
        this.counter = this.counter + 1;
        if (this.counter === 6) {
          alert('thank you for participating');
        }
        this.username = '';
        this.inputEl.nativeElement.focus();
      }
    }
  }

}
