
export class Experience {
  id:string;
  institutional:boolean;
  dateFrom:number;
  dateTo:number;
  educational:boolean;
  certifierId?:string;
  certified:boolean;
  categorization?:Map<string,string>;
  instituteId?:string;
  schoolYear?:string;
  registrationId?:string;
  constructor() {
      }
}
