import { Injectable }    from '@angular/core';
import {Experience } from '../classes/Experience.interface';
//import { Headers, Http } from '@angular/http';
//import 'rxjs/add/operator/toPromise';

@Injectable()
export class ExperienceService  {
   experiences: Experience[] = [];
  getExperiences(): Experience[] {
    return this.experiences;
  }
}
