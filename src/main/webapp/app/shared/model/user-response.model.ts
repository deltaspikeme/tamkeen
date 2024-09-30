import { type IAnswer } from '@/shared/model/answer.model';
import { type IQuestion } from '@/shared/model/question.model';
import { type IUser } from '@/shared/model/user.model';

export interface IUserResponse {
  id?: string;
  responseDate?: Date;
  answer?: IAnswer | null;
  question?: IQuestion | null;
  user?: IUser | null;
}

export class UserResponse implements IUserResponse {
  constructor(
    public id?: string,
    public responseDate?: Date,
    public answer?: IAnswer | null,
    public question?: IQuestion | null,
    public user?: IUser | null,
  ) {}
}
