import { Component, OnInit } from '@angular/core';
import { NavController, NavParams, ModalController, LoadingController } from 'ionic-angular';
import { UserService } from '../../services/user.service'
import { Stage } from '../../classes/Stage.class'
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
  selector: 'page-add-stage',
  templateUrl: 'addStage.html'
})


export class AddStagePage implements OnInit {
  studentExperience: StudentExperience = new StudentExperience();
  experienceContaniner: ExperienceContainer = new ExperienceContainer();
  //certification: Certification = new Certification();
  stage: Stage = new Stage();
  document: Document = new Document();
  documents: Document[] = [];
  delDocuments: Document[] = [];
  dateFrom = new Date().toISOString();
  dateTo = new Date().toISOString();
  uploader: FileUploader = new FileUploader({});
  stageForm: FormGroup;
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
    this.stageForm = formBuilder.group({
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
    this.stage.location = item.name;
    this.stage.geocode = [item.location[0], item.location[1]];
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
        this.stage.location = address.location;
        this.stage.geocode = [address.e.latlng.lng, address.e.latlng.lng]
      }
    });
    myModal.present();
  }
  ngOnInit(): void {
    let stage = this.params.get('stage');
    if (stage != null) {
      this.studentExperience = JSON.parse(this.params.get('stage'));
      this.experienceContaniner = this.studentExperience.experience;
      this.stage = this.experienceContaniner.attributes as Stage;
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

    return false;
  }

  removeOldDocument(doc, index) {
    // maintain list of documents to be deleted on save operation.
    this.delDocuments.push(this.documents[index]);
    this.documents.splice(index, 1);
    this.uploader.queue.splice(index, 1);
  }

  addStage(): void {

    this.submitAttempt = true;
    if (this.stageForm.valid) {
      this.stage.type = ExperienceTypes.EXP_TYPE_STAGE;
      // this.stage.duration = 10
      // this.stage.geocode = [0, 0]
      this.stage.dateFrom = new Date(this.dateFrom).getTime();
      this.stage.dateTo = new Date(this.dateTo).getTime();
      this.experienceContaniner.attributes = this.stage;
      this.studentExperience.experience = this.experienceContaniner;

      if (this.experienceContaniner.id != null) {
        this.userService.updateStage(this.studentExperience).then(stage => {
          this.experienceContaniner = stage;
          // check if there are documents to be deleted.
          var promisesDelDocuments: Promise<any>[] = [];
          for (var d = 0; d < this.delDocuments.length; d++) {
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
                this.utilsService.toast(this.translate.instant('toast_error_fields_missing'), 3000, 'middle');
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
          this.userService.addStage(this.studentExperience).then(certification => {
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

  discard(): void {
    this.navCtrl.pop();
  }


}
