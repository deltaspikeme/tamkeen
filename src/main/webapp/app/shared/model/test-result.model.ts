import { type IUser } from '@/shared/model/user.model';
import { type IPersonalityTest } from '@/shared/model/personality-test.model';
import { type IPersonalityType } from '@/shared/model/personality-type.model';

export interface ITestResult {
  id?: string;
  analysis?: string | null;
  resultDate?: Date;
  user?: IUser | null;
  personalityTest?: IPersonalityTest | null;
  personalityType?: IPersonalityType | null;
}

export class TestResult implements ITestResult {
  constructor(
    public id?: string,
    public analysis?: string | null,
    public resultDate?: Date,
    public user?: IUser | null,
    public personalityTest?: IPersonalityTest | null,
    public personalityType?: IPersonalityType | null,
  ) {}
}
