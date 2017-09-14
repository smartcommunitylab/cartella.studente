import { TeachingUnit } from './TeachingUnit'
import { Institute } from './Institute'

export interface Registration {
        classroom: string;
        course: string;
        courseId: string;
        creationDate: Date;
        dateFrom: Date;
        dateTo: Date;
        extId: string;
        id: string;
        lastUpdate: Date;
        origin: string;
        schoolYear: string;
        studentId: string;
        instituteId: Institute;
        teachingUnit: TeachingUnit;
        checked: boolean;
}
