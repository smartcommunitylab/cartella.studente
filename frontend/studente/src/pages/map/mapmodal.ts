import { Component, OnInit } from '@angular/core';
import { NavController, ModalController, ViewController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import {GeoService} from '../../services/geo.service'
import * as Leaflet from 'leaflet';


@Component({
  selector: 'map-modal',
  templateUrl: 'mapmodal.html'
})
export class MapModal implements OnInit {
  // public static viewCtrl = new ViewController();
  constructor(public navCtrl: NavController,
    public modalCtrl: ModalController,
    public viewCtrl: ViewController,
     public alertCtrl: AlertController,
    public translate: TranslateService,
    public GeoService:GeoService) {
    //this.viewCtrl=viewCtrl;
  }

  ngOnInit(): void {
    this.drawMap();
  }

  dismiss(e) {
    this.viewCtrl.dismiss(e);
  }
  drawMap(): void {
    let map = Leaflet.map('map');
    console.log(this.viewCtrl);
    Leaflet.tileLayer('https://api.mapbox.com/styles/v1/mapbox/streets-v10/tiles/256/{z}/{x}/{y}?access_token=pk.eyJ1IjoicGF0cmlja3IiLCJhIjoiY2l2aW9lcXlvMDFqdTJvbGI2eXUwc2VjYSJ9.trTzsdDXD2lMJpTfCVsVuA', {
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
      maxZoom: 18
    }).addTo(map);

    //web location
    map.locate({ setView: true });

    //when we have a location draw a marker and accuracy circle
    function onLocationFound(e) {
      var radius = e.accuracy / 2;

      Leaflet.marker(e.latlng).addTo(map);
      // .bindPopup("You are within " + radius + " meters from this point").openPopup();

      Leaflet.circle(e.latlng, radius).addTo(map);
    }
    map.addEventListener('locationfound', onLocationFound);
    //alert on location error
    function onLocationError(e) {
      console.log(e.message);
    }
    map.addEventListener('click', (e) => {
      //get address and then open popup
      //open confirm popUp
      this.GeoService.getAddressFromCoordinates(e).then(location => {
        let alert = this.alertCtrl.create({
          title: this.translate.instant('alert_map_location_title'),
          message: this.translate.instant('alert_map_location_message', { value: location }),
          buttons: [
            {
              text: this.translate.instant('alert_cancel'),
              cssClass: 'pop-up-button',
              role: 'cancel'

            },
            {
              text: this.translate.instant('alert_confirm'),
              cssClass: 'pop-up-button',
              handler: () => {
                this.dismiss({
                  e,location});
              }
            }
          ]
        });
        alert.present();
      });
    });
    map.on('locationerror', onLocationError);
  }
}
