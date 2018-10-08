import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { CurriculumPage } from '../../curriculum/curriculum';

@Component({
  selector: 'app-bar',
  templateUrl: 'app-bar.component.html'
})
export class AppBar {
  expanded: boolean = false;

  constructor(public navCtrl: NavController) { }

  toggleBar(): void {
    this.expanded = !this.expanded;
  }

  curriculum(): void {
    this.navCtrl.push(CurriculumPage);
  }

  cedus(): void {
    var url = 'https://dev.smartcommunitylab.it/cedus';
    var params = [
      'height=' + screen.height,
      'width=' + screen.width,
      // 'fullscreen=yes' // only works in IE, but here for completeness
    ].join(',');
    window.open(url, '_blank', params);
    
  }
}
