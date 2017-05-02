 export interface RegistrationInstitute {
    institute: {
      address: string;
      cf: string;
      creationDate: Date;
      description: string;
      email: string;
      extId: string;
      geocode: number[];
      id: string;
      lastUpdate: Date;
      name: string;
      origin: string;
      pec: string;
      phone: string
    };
    registrations: RegistrationInstitute[]
  }
