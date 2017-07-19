import { Injectable }    from '@angular/core';
import {Experience } from '../classes/Experience.class';
import {CertificationsTypes } from '../assets/conf/certificationsTypes';
import { TranslateService } from 'ng2-translate';

@Injectable()
export class ExperienceService  {
   experiences: Experience[] = [];
   cert_type_selection = [];
   constructor(public translate: TranslateService){

   this.cert_type_selection = [
        { text: this.translate.instant('cert_type_lang'), value: CertificationsTypes.CERT_TYPE_LANG},
        {text: this.translate.instant('cert_type_other'), value: CertificationsTypes.CERT_TYPE_OTHER}]
   }
  getExperiences(): Experience[] {
    return this.experiences;
  }

  getCertificationTypes() {
    return  this.cert_type_selection;
  }
}
