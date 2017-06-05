import { Component, OnInit } from '@angular/core';
import { NavController, NavParams, ModalController } from 'ionic-angular';
import { UserService } from '../../services/user.service'
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'
import { Certification } from '../../classes/Certification.class'
import { Certificate } from '../../classes/Certificate.class'
import { StudentExperience } from '../../classes/StudentExperience.class'
import { ExperienceContainer } from '../../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../../assets/conf/expTypes';
import { ConfigService } from '../../services/config.service'
import { FileUploader } from 'ng2-file-upload';
import { MapModal } from '../map/mapmodal'
import { GeoService } from '../../services/geo.service'
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UtilsService } from '../../services/utils.services'
import { TranslateService } from 'ng2-translate';

@Component({
  selector: 'page-add-certification',
  templateUrl: 'addCertification.html'
})


export class AddCertificationPage implements OnInit {
  studentExperience: StudentExperience = new StudentExperience();
  experienceContaniner: ExperienceContainer = new ExperienceContainer();
  certification: Certification = new Certification();
  certificate: Certificate = new Certificate();
  dateFrom = new Date().toISOString();
  dateTo = new Date().toISOString();
  items = [];
  certificationForm: FormGroup;
  submitAttempt = false;
  uploader: FileUploader = new FileUploader({});
  showList = false;

  constructor(public navCtrl: NavController,
    public params: NavParams,
    private userService: UserService,
    private config: ConfigService,
    private webAPIConnector: WebAPIConnectorService,
    public modalCtrl: ModalController,
    public GeoService: GeoService,
    public formBuilder: FormBuilder,
    public utilsService: UtilsService,
    public translate:TranslateService) {
    this.certificationForm = formBuilder.group({
      title: ['', Validators.compose([Validators.required])],
      // dateFrom: ['', Validators.compose([Validators.required])],
      // dateTo: ['', Validators.compose([Validators.required])],
      location: ['', Validators.compose([Validators.required])],
      // contact: ['', Validators.compose([Validators.required])],
      description: ['', Validators.compose([Validators.required])]
    });
  }

  selectPlace(item) {
    //set name and coordinates of the selected place
    this.certification.location = item.name;
    this.certification.geocode = [item.location[0], item.location[1]];
    //hide result when it is clicked on the element
    this.showList = false;
  }
  getItems(ev: any) {

    //get items from geocoder
    this.GeoService.getAddressFromString(ev.target.value).then(locations => {
      // set val to the value of the searchbar
      let val = ev.target.value;
      if (locations instanceof Array) {
        this.items = locations;
      }
      // if the value is an empty string don't filter the items
      if (val && val.trim() != '') {

        // Filter the items
        this.items = this.items.filter((item) => {
          return (item.name.toLowerCase().indexOf(val.toLowerCase()) > -1);
        });

        // Show the results
        this.showList = true;
      } else {

        // hide the results when the query is empty
        this.showList = false;
      }
    });
  }
  ngOnInit(): void {
    let certification = this.params.get('certification');
    if (certification != null) {
      this.studentExperience = JSON.parse(this.params.get('certification'));
      this.experienceContaniner = this.studentExperience.experience;
      this.certification = this.experienceContaniner.attributes as Certification;
      this.certificate = this.studentExperience.certificate as Certificate;
      this.dateFrom = new Date(this.experienceContaniner.attributes.dateFrom).toISOString();
      this.dateTo = new Date(this.experienceContaniner.attributes.dateTo).toISOString();
    }
  }
  chooseAddress(): void {
    let myModal = this.modalCtrl.create(MapModal);
    myModal.onDidDismiss(address => {
      if (address) {
        this.certification.location = address.location;
        this.certification.geocode = [address.e.latlng.lng, address.e.latlng.lng]
      }
    });
    myModal.present();
  }
  removeCertification(): void {
    this.uploader.clearQueue();
    (<HTMLInputElement>document.getElementById("uploadInputFile")).value = "";
  }
  removeActualCertificate(): void {
    this.userService.deleteCertificate(this.studentExperience).then(() =>
      this.certificate = null)
  }
  addCertification(): void {
    this.submitAttempt = true;
    if (this.certificationForm.valid) {
      this.certification.type = ExperienceTypes.EXP_TYPE_CERT;
      this.certification.dateFrom = new Date(this.dateFrom).getTime();
      this.certification.dateTo = new Date(this.dateTo).getTime();
      this.experienceContaniner.attributes = this.certification;
      this.studentExperience.experience = this.experienceContaniner;
      if (this.experienceContaniner.id != null) {
        this.userService.updateCertification(this.studentExperience).then(certification => {
          this.experienceContaniner = certification;
          if (this.uploader.queue.length > 0) {
            this.uploadCertificate(this.uploader.queue[0]).then(() => this.navCtrl.pop())
          } else {
            this.navCtrl.pop();
          }
        }
        );
      }
      else {
        this.userService.addCertification(this.studentExperience).then(certification => {
          this.experienceContaniner = certification;
          if (this.uploader.queue.length > 0) {
            this.uploadCertificate(this.uploader.queue[0]).then(() => this.navCtrl.pop())
          } else {
            this.navCtrl.pop();
          }
        }
        );
      }
    }
    //  else {
    // this.utilsService.toast( this.translate.instant('toast_error_fields_missing'),3000,'middle');
    // }
  }
  uploadCertificate(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this.userService.createCertificate(this.experienceContaniner).then(experienceId => {
        this.webAPIConnector.uploadCertificate(this.uploader, this.userService.getUserId(), experienceId, item);
        resolve();
      })
    })

  }
  discard(): void {
    this.navCtrl.pop();
  }
}
