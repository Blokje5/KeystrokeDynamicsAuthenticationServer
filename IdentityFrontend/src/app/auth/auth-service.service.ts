import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class AuthServiceService {

  private url = 'http://localhost:4567';

  constructor(private http: HttpClient) { }

  public login(username: string, password: string, keystrokeEvents): Observable<string> {
    const loginRequest = {
      username,
      password,
      keystrokeEvents
    };

    const httpOptions = new HttpHeaders({
      'Content-Type':  'application/json',
    });

    return this.http.post<string>(`${this.url}/login`, loginRequest, {
      headers: httpOptions
    });
  }

  public register(username: string, password: string, confirmPassword: string): Observable<any> {
    const loginRequest = {
      username,
      password,
      confirmPassword
    };

    const httpOptions = new HttpHeaders({
      'Content-Type':  'application/json',
    });

    return this.http.post(`${this.url}/registration`, loginRequest, {
      headers: httpOptions
    });
  }
}
