import { Certification } from './Certification.class';
import { Mobility } from './Mobility.class';
import { Stage } from './Stage.class';
import { Student } from './Student.class';

export class Curriculum {

    id: string;
    attachments: String[];
    creationDate: string;
    cvLangCertList: Certification[];
    cvMobilityList: Mobility[];
    cvStageList: Stage[];
    extId: string;
    lastUpdate: string;
    origin: string;
    registrationIdList: string[];
    storageIdList: string[];
    student: Student[];
    studentExperienceIdMap: {};
    studentId: string

}

