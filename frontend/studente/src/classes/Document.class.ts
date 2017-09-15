import {Experience }from './Experience.class'

export class Document {
attributes:Map<string,string>;
contentType:string;
documentPresent:boolean;
documentUri:string;
experienceId:string;
storageId:string;
studentId: string;
checked: boolean;
filename: string;
constructor(){
    this.attributes=new Map<string,string>();
    this.attributes['name']="";
}
}
