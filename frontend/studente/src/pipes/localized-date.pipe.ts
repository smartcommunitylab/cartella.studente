import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import {TranslateService} from 'ng2-translate';

@Pipe({
  name: 'localizedDate',
  pure: false
})
export class LocalizedDatePipe implements PipeTransform {

  constructor(private translateService: TranslateService) {
  }

  transform(value: any, pattern: string = 'mediumDate'): any {
    const datePipe: DatePipe = new DatePipe(this.translateService.getDefaultLang()); //'it'
    return datePipe.transform(value, pattern);
  }

}