import { DataCollectionService } from './../data/data-collection.service';
import { Injectable } from '@angular/core';
import { v4 as uuid } from 'uuid';

interface Sample {
  count: number;
  keystrokeEvents: Array<any>;
}

@Injectable()
export class KeystrokeDynamicsService {
  private sessionId;
  private sampleCounter = 0;
  private sampleArray: Array<Sample> = [];
  private eventArray = [];
  private username: string;

  public getUsername(): string {
    return this.username;
  }

  public getEventArray() {
    return this.eventArray;
  }

  constructor(private dcService: DataCollectionService) {
    this.sessionId = uuid();
  }

  public transmitSession(username: string) {
    this.username = username;
    this.dcService.transmitSession(username, this.sessionId).subscribe();
  }

  collectEvent(event) {
    const input = event.srcElement as HTMLInputElement;
    const eventTime = performance.now();
    const keyName = event.key;
    const keyCode = event.code;
    const eventType = event.type;

    const trackingEvent = {
      sessionId: this.sessionId,
      eventType,
      eventTime,
      keyName,
      keyCode,
      inputType: input.type,
      id: input.id
    };
    this.eventArray.push(trackingEvent);
  }

  onSample() {
    const sample = {
      count: this.sampleCounter,
      keystrokeEvents: this.eventArray
    };
    this.sampleArray.push(sample);

    this.eventArray = [];

    this.sampleCounter++;
    if (this.sampleCounter > 5) {
      this.onCompletion();
    }
  }

  onCompletion() {
    this.dcService.submitEvents(this.username, this.sampleArray).subscribe();

    this.sampleArray = [];
  }

  reset() {
    this.eventArray = [];
  }

}
