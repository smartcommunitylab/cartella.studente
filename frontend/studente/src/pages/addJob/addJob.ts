import { Component, OnInit } from '@angular/core';
import { NavController, NavParams, ModalController } from 'ionic-angular';
import { UserService } from '../../services/user.service'
import { Job } from '../../classes/Job.class'
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



@Component({
  selector: 'page-add-job',
  templateUrl: 'addJob.html'
})


export class AddJobPage implements OnInit {
  studentExperience: StudentExperience = new StudentExperience();
  experienceContaniner: ExperienceContainer = new ExperienceContainer();
  // certification: Certification = new Certification();
  job: Job = new Job();
  document: Document = new Document();
  dateFrom = new Date().toISOString();
  dateTo = new Date().toISOString();
  uploader: FileUploader = new FileUploader({});
  jobForm: FormGroup;
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
    this.jobForm = formBuilder.group({
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
    this.job.location = item.name;
    this.job.geocode = [item.location[0], item.location[1]];
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
        this.job.location = address.location;
        this.job.geocode = [address.e.latlng.lng, address.e.latlng.lng]
      }
    });
    myModal.present();
  }
  ngOnInit(): void {
    let job = this.params.get('job');
    if (job != null) {
      this.studentExperience = JSON.parse(this.params.get('job'));
      this.experienceContaniner = this.studentExperience.experience;
      // this.certification = this.experienceContaniner.attributes as Certification;
      this.document = this.studentExperience.document as Document;

      this.job = this.experienceContaniner.attributes as Job;
      this.dateFrom = new Date(this.experienceContaniner.attributes.dateFrom).toISOString();
      this.dateTo = new Date(this.experienceContaniner.attributes.dateTo).toISOString();
    }
  }
  removeCertification(): void {
    this.uploader.clearQueue();
    (<HTMLInputElement>document.getElementById("uploadInputFile")).value = "";
  }
  removeActualDocument(): void {
    this.userService.deleteDocument(this.studentExperience).then(() =>
      this.document = null)
  }
  addJob(): void {



    this.submitAttempt = true;
    if (this.jobForm.valid) {
      this.job.type = ExperienceTypes.EXP_TYPE_JOB;
      this.job.duration = 10
      this.job.geocode = [0, 0]
      this.job.dateFrom = new Date(this.dateFrom).getTime();
      this.job.dateTo = new Date(this.dateTo).getTime();
      this.experienceContaniner.attributes = this.job;
      this.studentExperience.experience = this.experienceContaniner;

      if (this.experienceContaniner.id != null) {
        this.userService.updateJob(this.studentExperience).then(job => {
          this.experienceContaniner = job;
          if (this.uploader.queue.length > 0) {
            this.uploadDocument(this.uploader.queue[0]).then(() => this.navCtrl.pop())
          } else {
            this.navCtrl.pop();
            this.utilsService.toast(this.translate.instant('toast_add_job'), 3000, 'middle');
          }
        }
        );
      }
      else {
        this.userService.addJob(this.studentExperience).then(job => {
          this.experienceContaniner = job;
          if (this.uploader.queue.length > 0) {
            this.uploadDocument(this.uploader.queue[0]).then(() => this.navCtrl.pop())
          } else {
            this.navCtrl.pop();
            this.utilsService.toast(this.translate.instant('toast_add_job'), 3000, 'middle');
          }
        }
        );
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
