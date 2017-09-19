
import { Injectable } from '@angular/core';
import { ToastController } from 'ionic-angular'

import { StudentExperience } from '../classes/StudentExperience.class'

@Injectable()
export class UtilsService {

    constructor( private toastCtrl: ToastController) { }
    toast(message:string,duration:number,position:string):void{
     let toast = this.toastCtrl.create({
                message: message,
                duration: duration,
                position: position
              });
              toast.present();
    }

    sortExperience(selectedValue: any, collection: StudentExperience[]): Promise<StudentExperience[]> {

        console.log('Selected order: ' + selectedValue);

        return new Promise<StudentExperience[]>((resolve, reject) => {

            switch (selectedValue) {

                case 'a-z':
                    collection.sort(function (a, b) {
                        if (a.experience.attributes.title < b.experience.attributes.title) return -1;
                        if (a.experience.attributes.title > b.experience.attributes.title) return 1;
                        return 0;
                    })
                    break;

                case 'z-a':
                    collection.sort(function (a, b) {
                        if (a.experience.attributes.title < b.experience.attributes.title) return 1;
                        if (a.experience.attributes.title > b.experience.attributes.title) return -1;
                        return 0;
                    })
                    break;

                case 'earliest':
                    collection.sort(function (a, b) {
                        if (a.experience.attributes.dateFrom > b.experience.attributes.dateFrom) return 1;
                        if (a.experience.attributes.dateFrom < b.experience.attributes.dateFrom) return -1;
                        return 0;
                    });
                    break;

                case 'latest':
                    collection.sort(function (a, b) {
                        if (a.experience.attributes.dateFrom > b.experience.attributes.dateFrom) return -1;
                        if (a.experience.attributes.dateFrom < b.experience.attributes.dateFrom) return 1;
                        return 0;
                    });
                    break;

                default:
                    alert('default');
                    collection.sort(function (a, b) {
                        if (a.experience.attributes.id < b.experience.attributes.id) return -1;
                        if (a.experience.attributes.id > b.experience.attributes.id) return 1;
                        return 0;
                    })

            }
            resolve(collection);
        });

    }    

}