import { Component, OnInit } from '@angular/core';
import { NavController, NavParams, ModalController } from 'ionic-angular';
import { UserService } from '../../services/user.service'
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'
import { ExperienceService } from '../../services/experience.service'
import { Certification } from '../../classes/Certification.class'
import { Document } from '../../classes/Document.class'
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
import { CertificationsTypes } from '../../assets/conf/certificationsTypes';
import { Observable } from 'rxjs/Rx';

// import { checkingDates } from '../../validators/validators';

@Component({
  selector: 'page-add-certification',
  templateUrl: 'addCertification.html'
})


export class AddCertificationPage implements OnInit {
  studentExperience: StudentExperience = new StudentExperience();
  experienceContaniner: ExperienceContainer = new ExperienceContainer();
  certification: Certification = new Certification();
  document: Document = new Document();
  documents: Document[] = [];
  dateFrom = new Date().toISOString();
  dateTo = new Date().toISOString();
  items = [];
  type = {};
  language = "";
  typesData = [];
  certificationForm: FormGroup;
  submitAttempt = false;
  uploader: FileUploader = new FileUploader({});
  showList = false;
  // documentEdit = false;

  constructor(public navCtrl: NavController,
    public params: NavParams,
    private userService: UserService,
    private config: ConfigService,
    private webAPIConnector: WebAPIConnectorService,
    private experienceService: ExperienceService,
    public modalCtrl: ModalController,
    public GeoService: GeoService,
    public formBuilder: FormBuilder,
    public utilsService: UtilsService,
    public translate: TranslateService) {
    this.certificationForm = formBuilder.group({
      title: ['', Validators.compose([Validators.required])],
      dateFrom: ['', Validators.compose([Validators.required])],
      dateTo: ['', Validators.compose([Validators.required])],
      location: ['', Validators.compose([Validators.required])],
      description: ['', Validators.compose([Validators.required])]
    }, { validator: this.checkingDates('dateFrom', 'dateTo') });

    this.typesData = experienceService.getCertificationTypes();
    this.type = this.typesData[0];

  }

  checkingDates(dateFromKey: string, dateToKey: string) {
    return (group: FormGroup): { [key: string]: any } => {
      let dateFrom = group.controls[dateFromKey];
      let dateTo = group.controls[dateToKey];
      var d1 = Date.parse(dateFrom.value);
      var d2 = Date.parse(dateTo.value);
      if (d1 > d2 || d1 > Date.now() || d2 > Date.now()) return {
        dateError: true
      };
    }
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
      this.documents = this.studentExperience.documents as Document[];
      this.dateFrom = new Date(this.experienceContaniner.attributes.dateFrom).toISOString();
      this.dateTo = new Date(this.experienceContaniner.attributes.dateTo).toISOString();
    }
  }
  addDocument() {
    // this.documentEdit= true;
    //check if theere are info for doc and in that case add new doc in documents
    if (this.document && this.checkDocumentParams()) {
      this.documents.push(this.document)
      this.document = new Document();
       (<HTMLInputElement>document.getElementById("uploadInputFile")).value = "";
    } else {
      this.utilsService.toast( this.translate.instant('toast_error_fields_missing'),3000,'middle')
    }

  }
  checkDocumentParams(): boolean {
    if (this.document.attributes['name'] == "") {
      return false
    }
    if (this.uploader.queue.length==(this.documents.length+1)){
      this.document.documentUri=this.uploader.queue[this.uploader.queue.length-1].file.name;
      return true;
    }
    return false;
  }
  isLanguage(): boolean {
    if (this.type['value'] && this.type['value'] == CertificationsTypes.CERT_TYPE_LANG) {
      return true
    }
    return false
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

  removeOldDocument (doc,index) {
    this.documents.splice(index,1);
    this.uploader.queue.splice(index,1);

  }
  // removeCertification(): void {
  //   this.uploader.clearQueue();
  //   (<HTMLInputElement>document.getElementById("uploadInputFile")).value = "";
  // }
  // removeActualDocument(): void {
  //   this.userService.deleteDocument(this.studentExperience).then(() =>
  //     this.document = null)
  // }
  addCertification(): void {
    this.submitAttempt = true;
    console.log(!this.certificationForm.controls.location.valid);
    console.log(this.certification.geocode);
    console.log((this.certificationForm.controls.location.dirty || this.submitAttempt));
    console.log((!this.certificationForm.controls.location.valid && (this.certificationForm.controls.location.dirty || this.submitAttempt) && !this.certification.geocode));
    if (this.certificationForm.valid) {
      this.certification.type = this.type['value'];
      this.certification.dateFrom = new Date(this.dateFrom).getTime();
      this.certification.dateTo = new Date(this.dateTo).getTime();
      this.experienceContaniner.attributes = this.certification;
      this.studentExperience.experience = this.experienceContaniner;
      if (this.experienceContaniner.id != null) {
        this.userService.updateCertification(this.studentExperience).then(certification => {
          this.experienceContaniner = certification;
          // map them into a array of observables and forkJoin
          Observable.forkJoin(
            this.uploader.queue.map(
              i => this.uploadDocument(i).then(() =>{
                console.log("uploaded");
              })
            )
          ).subscribe(() => this.navCtrl.pop())
          // if (this.uploader.queue.length > 0) {
          //   this.uploadDocument(this.uploader.queue[0]).then(() => this.navCtrl.pop())
          // } else {
          //   this.navCtrl.pop();
          // }
        }
        );
      }
      else {
        this.userService.addCertification(this.studentExperience).then(certification => {
          this.experienceContaniner = certification;
          Observable.forkJoin(
            this.uploader.queue.map(
              i => this.uploadDocument(i).then(() =>{

               console.log("uploaded");
              })
            )
          ).subscribe(() => this.navCtrl.pop())
          // if (this.uploader.queue.length > 0) {
          //   this.uploadDocument(this.uploader.queue[0]).then(() => this.navCtrl.pop())
          // } else {
          //   this.navCtrl.pop();
          // }
        }
        );
      }
    }
    //  else {
    // this.utilsService.toast( this.translate.instant('toast_error_fields_missing'),3000,'middle');
    // }
  }
  uploadDocument(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this.userService.createDocument(this.experienceContaniner).then(exp => {
        this.webAPIConnector.uploadDocument(this.uploader, this.userService.getUserId(), exp.experienceId, item, exp.storageId);
        resolve();
      })
    })

  }
  discard(): void {
    this.navCtrl.pop();
  }
}
