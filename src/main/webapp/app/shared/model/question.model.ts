import { type IPersonalityTest } from '@/shared/model/personality-test.model';

import { type QuestionType } from '@/shared/model/enumerations/question-type.model';
export interface IQuestion {
  id?: string;
  questionText?: string;
  questionType?: keyof typeof QuestionType;
  personalityTest?: IPersonalityTest | null;
}

export class Question implements IQuestion {
  constructor(
    public id?: string,
    public questionText?: string,
    public questionType?: keyof typeof QuestionType,
    public personalityTest?: IPersonalityTest | null,
  ) {}
}
