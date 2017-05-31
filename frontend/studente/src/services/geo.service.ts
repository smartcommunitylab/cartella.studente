import { Injectable } from '@angular/core'
import { Http } from '@angular/http'
import { ConfigService } from './config.service'
import { DefaultRequestOptions } from './webAPIConnector.service'

@Injectable()
export class GeoService {
    constructor(private http: Http, private config: ConfigService) {

    }
    getAddressFromCoordinates(location): Promise<any> {
        let options = new DefaultRequestOptions();
        var url = this.config.getConfig('geocoderUrl') + 'location?latlng=' + location.latlng.lat + ',' + location.latlng.lng;
        return this.http
            .get(url, options).toPromise()
            .then(response => {
                let places = response.json().response.docs;
                let name = '';
                if (places[0]) {
                    return this.getNameFromComplex(places[0])
                }
                return null;
            })
            .catch(response => this.handleError);
    }
    getAddressFromString(locationString:string): Promise<any> {
        // var names = [];
        let options = new DefaultRequestOptions();
        var url = this.config.getConfig('geocoderUrl') + 'address?latlng=' + this.config.getConfig('mapPositionAutocomplete')[0] + "," + this.config.getConfig('mapPositionAutocomplete')[1] + "&distance=" + this.config.getConfig('distanceAutocomplete') + "&address=" + locationString;
        return this.http
            .get(url, options).toPromise()
            .then(response => {
                let places = response.json().response.docs;
                // let geoCoderPlaces =[];
                return this.createPlaces(places);
               
            })
            .catch(response => this.handleError);



    }

    createPlaces = function (places){
        let k = 0;
        let geoCoderPlaces =[]
          for (var i = 0; i < places.length; i++) {
            let temp = '';
            if (places[i].name)
              temp = temp + places[i].name;
            if (places[i].street != places[i].name)
              if (places[i].street) {
                if (temp)
                  temp = temp + ', ';
                temp = temp + places[i].street;
              }
            if (places[i].housenumber) {
              if (temp)
                temp = temp + ', ';
              temp = temp + places[i].housenumber;
            }
            if (places[i].city) {
              if (temp)
                temp = temp + ', ';
              temp = temp + places[i].city;
            }

            //check se presente
            if (!geoCoderPlaces[temp]) {
              //se non presente
              k++
              geoCoderPlaces[k] = {
                name:temp,
                location: places[i].coordinate.split(',')
              }
            }
          }
          return geoCoderPlaces; 
    }
    getNameFromComplex = function (data) {
        let name = '';
        if (data) {
            if (data.name) {
                name = name + data.name;
            }
            if (data.street && (data.name != data.street)) {
                if (name)
                    name = name + ', ';
                name = name + data.street;
            }
            if (data.housenumber) {
                if (name)
                    name = name + ', ';
                name = name + data.housenumber;
            }
            if (data.city) {
                if (name)
                    name = name + ', ';
                name = name + data.city;
            }
            return name;
        }
    }
    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error);
        return Promise.reject(error);
    }
}
