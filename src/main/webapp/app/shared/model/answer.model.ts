import { type IQuestion } from '@/shared/model/question.model';

export interface IAnswer {
  id?: string;
  answerText?: string;
  score?: number;
  question?: IQuestion | null;
}

export class Answer implements IAnswer {
  constructor(
    public id?: string,
    public answerText?: string,
    public score?: number,
    public question?: IQuestion | null,
  ) {}
}
