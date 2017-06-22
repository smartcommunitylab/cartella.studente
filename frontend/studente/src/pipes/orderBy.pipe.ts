import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'orderBy'
})
export class OrderBy{

 transform(array, orderBy, asc = true){
 
     if (!orderBy || orderBy.trim() == ""){
       return array;
     } 
 
     //ascending
     if (asc){
       return Array.from(array).sort((item1: any, item2: any) => { 
         return this.orderByComparator(this.resolve(item1,orderBy), this.resolve(item2,orderBy));
       });
     }
     else{
       //not asc
       return Array.from(array).sort((item1: any, item2: any) => { 
         return this.orderByComparator(this.resolve(item2,orderBy), this.resolve(item1,orderBy));
       });
     }
 
 }
 
 orderByComparator(a:any, b:any):number{
 
     if((isNaN(parseFloat(a)) || !isFinite(a)) || (isNaN(parseFloat(b)) || !isFinite(b))){
       //Isn't a number so lowercase the string to properly compare
       if(a.toLowerCase() < b.toLowerCase()) return -1;
       if(a.toLowerCase() > b.toLowerCase()) return 1;
     }
     else{
       //Parse strings as numbers to compare properly
       if(parseFloat(a) < parseFloat(b)) return -1;
       if(parseFloat(a) > parseFloat(b)) return 1;
      }
 
     return 0; //equal each other
 }
 resolve(o, s) {
    s = s.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
    s = s.replace(/^\./, '');           // strip a leading dot
    var a = s.split('.');
    for (var i = 0, n = a.length; i < n; ++i) {
        var k = a[i];
        if (k in o) {
            o = o[k];
        } else {
            return;
        }
    }
    return o;
}
}