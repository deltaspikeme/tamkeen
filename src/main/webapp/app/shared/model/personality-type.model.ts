export interface IPersonalityType {
  id?: string;
  typeCode?: string;
  description?: string | null;
  strengths?: string | null;
  weaknesses?: string | null;
}

export class PersonalityType implements IPersonalityType {
  constructor(
    public id?: string,
    public typeCode?: string,
    public description?: string | null,
    public strengths?: string | null,
    public weaknesses?: string | null,
  ) {}
}
