export interface IPersonalityTest {
  id?: string;
  testName?: string;
  description?: string | null;
}

export class PersonalityTest implements IPersonalityTest {
  constructor(
    public id?: string,
    public testName?: string,
    public description?: string | null,
  ) {}
}
