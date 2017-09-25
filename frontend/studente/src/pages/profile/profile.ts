import { Component, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { UserService } from '../../services/user.service';
import { LoginService } from '../../services/login.service';
import { MyApp } from '../../app/app.component';
import { Student } from '../../classes/Student.class';
import { TranslateService } from 'ng2-translate';
import { FileUploader } from 'ng2-file-upload';
@Component({
  selector: 'page-activities',
  templateUrl: 'profile.html'
})
export class ProfilePage implements OnInit {
  student: Student = new Student();
  editMode = false;
  loader = null;
  profilePicture: string = "";
  uploader: FileUploader = new FileUploader({});

  constructor(public navCtrl: NavController,
    public params: NavParams,
    private userService: UserService,
    private loginService: LoginService,
    public loading: LoadingController,
    private translate: TranslateService,
    private alertCtrl: AlertController) {
  }
  ngOnInit(): void {
    this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      //this.student.imageUrl=response; 
      this.uploader.clearQueue();  
      this.userService.getUserImage().then(url =>
      {
         this.profilePicture = url;
         }
      );
      this.hideSpinner();
      this.editMode = false;
    };
  }
  removePicture(): void {
    this.uploader.clearQueue();
    (<HTMLInputElement>document.getElementById("uploadInputFile")).value = "";
  }

  openEditMode() {
    this.editMode = true;
  }
  getProfileImage() {
    return this.userService.getUserImage();
  }
  closeEditMode() {
    //sicuro di non voler salvare i dati?
    this.uploader.clearQueue();
    let alert = this.alertCtrl.create({
      title: this.translate.instant('alert_delete_certification_title'),
      message: this.translate.instant('alert_delete_certification_message'),
      buttons: [
        {
          text: this.translate.instant('alert_cancel'),
          role: 'cancel',
          cssClass: 'pop-up-button'

        },
        {
          text: this.translate.instant('alert_confirm'),
          cssClass: 'pop-up-button',
          handler: () => {
            //return do read mode
            this.editMode = false;
          }
        }
      ]
    });
    alert.present();
  }

  saveData() {
    this.showSpinner();
    this.userService.saveUserInfo(this.student).then(student => {
      this.student = student;
      if (this.uploader.queue.length > 0) {
        this.userService.sendUserImage(this.uploader, this.uploader.queue[0]);
      }
      else {
        this.hideSpinner();
        this.editMode = false;
      }
    });
  }
  ionViewWillEnter() {
    this.showSpinner();
    this.userService.getUserInfo().then(student => {
      this.student = student;
      this.userService.getUserImage().then(url =>
      { this.profilePicture = url; }
      );
      this.hideSpinner()
    }
    );
  }


  private showSpinner() {
    this.loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
    this.loader.present().catch(() => { });
  }

  private hideSpinner() {
    if (this.loading !== undefined) {
      this.loader.dismiss().catch(() => { });
    }
  }

  logout() {
    this.loginService.logout().then(res => {
      console.log("user logged out from client.");
      this.loginService.serverLogout();
    }).catch(error => {
      console.error("user not logged out from server.");
    })
  }

}
