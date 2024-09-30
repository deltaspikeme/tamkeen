import { type IConsultant } from '@/shared/model/consultant.model';

export interface IConsultation {
  id?: string;
  consultantName?: string;
  consultationDate?: Date;
  notes?: string | null;
  consultant?: IConsultant | null;
}

export class Consultation implements IConsultation {
  constructor(
    public id?: string,
    public consultantName?: string,
    public consultationDate?: Date,
    public notes?: string | null,
    public consultant?: IConsultant | null,
  ) {}
}
