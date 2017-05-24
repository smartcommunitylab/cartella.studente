import { Component } from '@angular/core';

@Component({
  selector: 'app-bar',
  templateUrl: 'app-bar.component.html'
})
export class AppBar {
  expanded:boolean=false;
  toggleBar():void {
    this.expanded=!this.expanded;
  }
}
