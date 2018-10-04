import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class DataCollectionService {

  private url = 'http://localhost:4567';

  constructor(private http: HttpClient) { }

  public submitEvents(username: String, samples: Array<any>) {
    const httpOptions = new HttpHeaders({
      'Content-Type':  'application/json',
    });

    const userTemplate = {
      username,
      samples
    };

    console.log(userTemplate);

    return this.http.post(`${this.url}/user-template`, userTemplate, {
      headers: httpOptions
    });
  }

  public transmitSession(username: String, session: String) {
    const httpOptions = new HttpHeaders({
      'Content-Type':  'application/json',
    });

    const sessionData = {
      username,
      session
    };

    return this.http.post(`${this.url}/session`, sessionData, {
      headers: httpOptions
    });
  }
}
