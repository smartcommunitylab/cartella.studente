import { Component, OnInit } from '@angular/core';
import { NavController, NavParams, ModalController, LoadingController } from 'ionic-angular';
import { UserService } from '../../services/user.service'
import { Mobility } from '../../classes/Mobility.class'
import { StudentExperience } from '../../classes/StudentExperience.class'
import { Document } from '../../classes/Document.class'
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
import { Observable } from 'rxjs/Rx';


@Component({
  selector: 'page-add-mobility',
  templateUrl: 'addMobility.html'
})


export class AddMobilityPage implements OnInit {
  studentExperience: StudentExperience = new StudentExperience();
  experienceContaniner: ExperienceContainer = new ExperienceContainer();
  //certification: Certification = new Certification();
  mobility: Mobility = new Mobility();
  document: Document = new Document();
  documents: Document[] = [];
  delDocuments: Document[] = [];
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
    public formBuilder: FormBuilder,
    public loading: LoadingController) {
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
      this.mobility = this.experienceContaniner.attributes as Mobility;
      this.documents = this.studentExperience.documents as Document[];
      this.delDocuments = [];
      this.dateFrom = new Date(this.experienceContaniner.attributes.dateFrom).toISOString();
      this.dateTo = new Date(this.experienceContaniner.attributes.dateTo).toISOString();
    }
    // this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
    //   //upload of document is finish so come back
    //   this.navCtrl.pop();
    // };
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

    return false;
  }

  removeOldDocument(doc, index) {
    // maintain list of documents to be deleted on save operation.
    this.delDocuments.push(this.documents[index]);
    this.documents.splice(index, 1);
    this.uploader.queue.splice(index, 1);
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
          // check if there are documents to be deleted.
          var promisesDelDocuments: Promise<any>[] = [];
          for (var d = 0; d < this.delDocuments.length; d++) {
            //promisesDelDocuments.push(this.deleteDocument(this.delDocuments[d]));
            promisesDelDocuments.push(this.userService.deleteDocumentInPromise(this.studentExperience.experienceId, this.delDocuments[d].storageId));
          }

          Promise.all(promisesDelDocuments).then(values => {
            console.log("PROMISE DELETE ALL.")
            // clear delete document list.
            this.delDocuments = [];
            var promisesUploadDocuments: Promise<any>[] = [];
            this.uploader.queue.map(i => {
              promisesUploadDocuments.push(this.userService.uploadDocumentInPromise(this.uploader, i, this.experienceContaniner));
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
          this.userService.addMobility(this.studentExperience).then(certification => {
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

  uploadDocument(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this.userService.createDocument(this.experienceContaniner).then(experienceId => {
        this.webAPIConnectorService.uploadDocument(this.uploader, this.userService.getUserId(), experienceId, item);
        resolve();
      })
    })

  }
  discard(): void {
    this.navCtrl.pop();
  }

}
