import { Component, OnInit } from '@angular/core';
import { NavController, NavParams, ModalController } from 'ionic-angular';
import { UserService } from '../../services/user.service'
import { Mobility } from '../../classes/Mobility.class'
import { StudentExperience } from '../../classes/StudentExperience.class'
import { Certificate } from '../../classes/Certificate.class'
import { Certification } from '../../classes/Certification.class'
import { ExperienceContainer } from '../../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../../assets/conf/expTypes'
import { ConfigService } from '../../services/config.service'
import { FileUploader } from 'ng2-file-upload';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'
import { MapModal } from '../map/mapmodal'
import { GeoService } from '../../services/geo.service'
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'



@Component({
  selector: 'page-add-mobility',
  templateUrl: 'addMobility.html'
})


export class AddMobilityPage implements OnInit {
  studentExperience: StudentExperience = new StudentExperience();
  experienceContaniner: ExperienceContainer = new ExperienceContainer();
  certification: Certification = new Certification();
  mobility: Mobility = new Mobility();
  certificate: Certificate = new Certificate();
  dateFrom = new Date().toISOString();
  dateTo = new Date().toISOString();
  uploader: FileUploader = new FileUploader({});
  mobilityForm: FormGroup;
  submitAttempt = false;
  showList = false;
  items = [];
  constructor(public navCtrl: NavController,
    public params: NavParams,
    private userService: UserService,
    private config: ConfigService,
    private utilsService: UtilsService,
    private translate: TranslateService,
    private webAPIConnectorService: WebAPIConnectorService,
    public modalCtrl: ModalController,
    public GeoService: GeoService,
    public formBuilder: FormBuilder) {
    this.mobilityForm = formBuilder.group({
      title: ['', Validators.compose([Validators.required])],
      // dateFrom: ['', Validators.compose([Validators.required])],
      // dateTo: ['', Validators.compose([Validators.required])],
      location: ['', Validators.compose([Validators.required])],
      lang: ['', Validators.compose([Validators.required])],
      // contact: ['', Validators.compose([Validators.required])],
      description: ['', Validators.compose([Validators.required])]
    });
  }
  selectPlace(item) {
    //set name and coordinates of the selected place
    this.mobility.location = item.name;
    this.mobility.geocode = [item.location[0], item.location[1]];
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
  chooseAddress(): void {
    let myModal = this.modalCtrl.create(MapModal);
    myModal.onDidDismiss(address => {
      if (address) {
        this.mobility.location = address.location;
        this.mobility.geocode = [address.e.latlng.lng, address.e.latlng.lng]
      }
    });
    myModal.present();
  }
  ngOnInit(): void {
    let mobility = this.params.get('mobility');
    if (mobility != null) {
      this.studentExperience = JSON.parse(this.params.get('mobility'));
      this.experienceContaniner = this.studentExperience.experience;
      this.certification = this.experienceContaniner.attributes as Certification;
      this.certificate = this.studentExperience.certificate as Certificate;

      this.mobility = this.experienceContaniner.attributes as Mobility;
      this.dateFrom = new Date(this.experienceContaniner.attributes.dateFrom).toISOString();
      this.dateTo = new Date(this.experienceContaniner.attributes.dateTo).toISOString();
    }
     this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
     //upload of certificate is finish so come back
     this.navCtrl.pop();
    };
  }
  removeCertification(): void {
    this.uploader.clearQueue();
    (<HTMLInputElement>document.getElementById("uploadInputFile")).value = "";
  }
  removeActualCertificate(): void {
    this.userService.deleteCertificate(this.studentExperience).then(() =>
      this.certificate = null)
  }
  addMobility(): void {



    this.submitAttempt = true;
    if (this.mobilityForm.valid) {
      this.mobility.type = ExperienceTypes.EXP_TYPE_STAGE;
      this.mobility.dateFrom = new Date(this.dateFrom).getTime();
      this.mobility.dateTo = new Date(this.dateTo).getTime();
      this.experienceContaniner.attributes = this.mobility;
      this.studentExperience.experience = this.experienceContaniner;

      if (this.experienceContaniner.id != null) {
        this.userService.updateMobility(this.studentExperience).then(mobility => {
          this.experienceContaniner = mobility;
          if (this.uploader.queue.length > 0) {
            this.uploadCertificate(this.uploader.queue[0]);
          } else {
            this.navCtrl.pop();
            this.utilsService.toast(this.translate.instant('toast_add_mobility'), 3000, 'middle');
          }
        }
        );
      }
      else {
        this.userService.addMobility(this.studentExperience).then(mobility => {
          this.experienceContaniner = mobility;
          if (this.uploader.queue.length > 0) {
            this.uploadCertificate(this.uploader.queue[0]);
          } else {
            this.navCtrl.pop();
            this.utilsService.toast(this.translate.instant('toast_add_mobility'), 3000, 'middle');
          }
        }
        );
      }
    }
  }
  uploadCertificate(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this.userService.createCertificate(this.experienceContaniner).then(experienceId => {
        this.webAPIConnectorService.uploadCertificate(this.uploader, this.userService.getUserId(), experienceId, item);
        resolve();
      })
    })

  }
  discard(): void {
    this.navCtrl.pop();
  }
}
