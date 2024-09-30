import { type IUser } from '@/shared/model/user.model';

export interface IConsultant {
  id?: string;
  name?: string;
  expertise?: string;
  bio?: string | null;
  email?: string;
  phone?: string | null;
  servicesOffered?: string | null;
  user?: IUser | null;
}

export class Consultant implements IConsultant {
  constructor(
    public id?: string,
    public name?: string,
    public expertise?: string,
    public bio?: string | null,
    public email?: string,
    public phone?: string | null,
    public servicesOffered?: string | null,
    public user?: IUser | null,
  ) {}
}
