import { Component, OnInit } from '@angular/core';
import { NavController, NavParams, ModalController, LoadingController, ViewController } from 'ionic-angular';
import { UserService } from '../../services/user.service'
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'
import { ExperienceService } from '../../services/experience.service'
import { Certification } from '../../classes/Certification.class'
import { Document } from '../../classes/Document.class'
import { StudentExperience } from '../../classes/StudentExperience.class'
import { ExperienceContainer } from '../../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../../assets/conf/expTypes';
import { ConfigService } from '../../services/config.service'
import { FileUploader, FileItem } from 'ng2-file-upload';
import { MapModal } from '../map/mapmodal'
import { GeoService } from '../../services/geo.service'
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UtilsService } from '../../services/utils.services'
import { TranslateService } from 'ng2-translate';
import { CertificationsTypes } from '../../assets/conf/certificationsTypes';
import { Observable } from 'rxjs/Rx';
import { DatePicker } from 'ionic2-date-picker';


@Component({
  selector: 'page-add-certification',
  templateUrl: 'addCertification.html',
  providers: [DatePicker]
})


export class AddCertificationPage implements OnInit {
  studentExperience: StudentExperience = new StudentExperience();
  experienceContaniner: ExperienceContainer = new ExperienceContainer();
  certification: Certification = new Certification();
  document: Document = new Document();
  documents: Document[] = [];
  delDocuments: Document[] = [];
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
    public translate: TranslateService,
    public loading: LoadingController,
    public viewCtrl: ViewController,
    public datePickerFrom: DatePicker,
    public datePickerTo: DatePicker) {
    
    
    //dateFrom.
    this.datePickerFrom = new DatePicker(<any>this.modalCtrl, <any>this.viewCtrl);
    this.datePickerFrom.onDateSelected.subscribe((date) => {
      this.dateFrom = date;
    });

    //dateTo.
    this.datePickerTo = new DatePicker(<any>this.modalCtrl, <any>this.viewCtrl);
    this.datePickerTo.onDateSelected.subscribe((date) => {
      this.dateTo = date;
    })

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

  showCalendarDateFrom() {
    this.datePickerFrom.showCalendar();
  }

  showCalendarDateTo() {
    this.datePickerTo.showCalendar();
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
      this.delDocuments = [];
      this.dateFrom = new Date(this.experienceContaniner.attributes.dateFrom).toISOString();
      this.dateTo = new Date(this.experienceContaniner.attributes.dateTo).toISOString();
    }
  }
  addDocument() {
    //check if theere are info for doc and in that case add new doc in documents
    if (this.document && this.checkDocumentParams()) {
      this.documents.push(this.document)
      this.document = new Document();
      (<HTMLInputElement>document.getElementById("uploadInputFile")).value = "";
    } else {
      this.utilsService.toast(this.translate.instant('toast_error_fields_missing'), 3000, 'middle')
    }

  }
  checkDocumentParams(): boolean {
    if (!this.document.attributes['title'] || this.document.attributes['title'] == "") {
      return false
    }

    if (this.uploader.queue.length > 0 && this.uploader.queue[this.uploader.queue.length - 1].file.name) {
      this.document.documentUri = this.uploader.queue[this.uploader.queue.length - 1].file.name;
      return true;
    }

    // if (this.uploader.queue.length==(this.documents.length+1)){
    //   this.document.documentUri=this.uploader.queue[this.uploader.queue.length-1].file.name;
    //   return true;
    // }

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

  removeOldDocument(doc, index) {
    // maintain list of documents to be deleted on save operation.
    this.delDocuments.push(this.documents[index]);
    this.documents.splice(index, 1);
    this.uploader.queue.splice(index, 1);
  }

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

          // check if there are documents to be deleted.
          var promisesDelDocuments: Promise<any>[] = [];
          for (var d = 0; d < this.delDocuments.length; d++) {
            //promisesDelDocuments.push(this.deleteDocument(this.delDocuments[d]));
            if (this.delDocuments[d].storageId) { //with this check we make sure to not call delete for intermediate selection and deletion.
              promisesDelDocuments.push(this.userService.deleteDocumentInPromise(this.studentExperience.experienceId, this.delDocuments[d].storageId));
            }
          }
          
          Promise.all(promisesDelDocuments).then(values => {
            console.log("PROMISE DELETE ALL.")
            // clear delete document list.
            this.delDocuments = [];
            var promisesUploadDocuments: Promise<any>[] = [];
            this.uploader.queue.map(i => {
              var temp: FileUploader = new FileUploader({});
              temp.queue.push(i);
              promisesUploadDocuments.push(this.userService.uploadDocumentInPromise(temp, i, this.experienceContaniner));
            })
            

            let loader = this.loading.create({
              content: this.translate.instant('loading'),
            });
            loader.present().then(() => {
              Promise.all(promisesUploadDocuments).then(values => {
                console.log("popping now")
                loader.dismiss();
                this.navCtrl.pop();
              }).catch(error => {
                loader.dismiss();
                this.utilsService.toast(this.translate.instant('toast_error'), 3000, 'middle');
              })
            })
          });
        });
      }
      else {
        let loader = this.loading.create({
          content: this.translate.instant('loading'),
        });
        loader.present().then(() => {
          this.userService.addCertification(this.studentExperience).then(certification => {
            this.experienceContaniner = certification;
            var promisesUploadDocuments: Promise<any>[] = [];
            this.uploader.queue.map(i => {
              // promisesUploadDocuments.push(this.uploadDocument(i));
              promisesUploadDocuments.push(this.userService.uploadDocumentInPromise(this.uploader, i, this.experienceContaniner));
            })
            
            if (promisesUploadDocuments.length > 0) {
              Observable.forkJoin(promisesUploadDocuments).subscribe(values => {
                console.log(values);
                loader.dismiss();
                this.navCtrl.pop()
              })
            } else {
              loader.dismiss();
            }
          });
        });
      }
    }
   }

  // uploadDocument(item): Promise<any> {
  //   return new Promise<any>((resolve, reject) => {
  //     this.userService.createDocument2(this.experienceContaniner, item).then(exp => {
  //       this.webAPIConnector.uploadDocumentWithPromise(this.uploader, this.userService.getUserId(), exp.experienceId, item, exp.storageId).then(resp => {
  //         resolve(true);
  //       }).catch(error => {
  //         return this.handleError;
  //       })
  //     }).catch(error => {
  //       return this.handleError;
  //     })
  //   })

  // }

  // deleteDocument(item): Promise<any> {
  //   return new Promise<any>((resolve, reject) => {
  //     this.webAPIConnector.deleteStudentDocumentFile(this.userService.getUserId(), this.studentExperience.experienceId, item.storageId).then(resp => {
  //       resolve();
  //     }).catch(error => {
  //       return this.handleError;
  //     })
  //   }).catch(error => {
  //     return this.handleError;
  //   })

  // }

  discard(): void {
    this.navCtrl.pop();
  }

}
