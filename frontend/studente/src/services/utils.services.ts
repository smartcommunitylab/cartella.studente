
import { Injectable } from '@angular/core';
import { ToastController } from 'ionic-angular'
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

}