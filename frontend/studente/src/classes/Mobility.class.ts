import { Experience } from './Experience.class'

export class Mobility extends Experience {
    title: string;
    description: string;
    type: string;
    duration: number;
    location: string;
    geocode: number[];
    lang: string;
}
