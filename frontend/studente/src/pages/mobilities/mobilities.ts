import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { UserService } from '../../services/user.service';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { AddMobilityPage } from '../addMobility/addMobility';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'
@Component({
  selector: 'page-mobilities',
  templateUrl: 'mobilities.html'
})
export class MobilitiesPage {
  mobilities: StudentExperience[] = [];
  order: string = "latest";
  icon = "ios-arrow-down";
  shownMobility = null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
  }

  addNewMobility(): void {
    this.navCtrl.push(AddMobilityPage);
  }

  onDeleted(mobilityId: string) {
    for (var i = 0; i < this.mobilities.length; i++) {
      if (this.mobilities[i].experience.id == mobilityId) {
        this.mobilities.splice(i, 1);
      }
    }
  }

  ionViewWillEnter() {
    let loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
    loader.present().then(() => {
      this.userService.getUserMobilities().then(mobilities => {
        this.utilsService.sortExperience(this.order, mobilities).then(sortedList => {
          this.mobilities = sortedList;
        })
        loader.dismiss();
      })
    })
  }

  onSelectChange(selectedValue: any) {
    this.utilsService.sortExperience(selectedValue, this.mobilities).then(sortedList => {
      this.mobilities = sortedList;
    })
  }

}
