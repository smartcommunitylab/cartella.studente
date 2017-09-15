import { Registration } from './Registration'
import { TeachingUnit } from './TeachingUnit'

export interface UserRegistration {
    teachingUnit: TeachingUnit;
    registrations: Registration[];
}