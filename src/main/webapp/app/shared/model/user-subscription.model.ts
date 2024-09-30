import { type IUser } from '@/shared/model/user.model';

import { type SubscriptionType } from '@/shared/model/enumerations/subscription-type.model';
export interface IUserSubscription {
  id?: string;
  subscriptionType?: keyof typeof SubscriptionType;
  startDate?: Date;
  endDate?: Date;
  user?: IUser | null;
}

export class UserSubscription implements IUserSubscription {
  constructor(
    public id?: string,
    public subscriptionType?: keyof typeof SubscriptionType,
    public startDate?: Date,
    public endDate?: Date,
    public user?: IUser | null,
  ) {}
}
