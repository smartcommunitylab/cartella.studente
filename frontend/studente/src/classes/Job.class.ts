import {Experience }from './Experience.class'

export class Job  extends Experience{
title	:string;
description	:string;
type	:string;
duration: number;
location	:string;
geocode	:number[]
contact	:string;
constructor() {
  super();
    }
}
