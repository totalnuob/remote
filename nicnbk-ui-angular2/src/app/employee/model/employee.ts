export class Employee {

    id: any;

    firstName: string;
    lastName: string;
    patronymic: string;
    birthDate: Date;

    position: any;
    username: string;
    email: string;
    active: boolean;

    lastNameRu: string;
    firstNameRu: string;
    patronymicRu: string;
    lastNameRuPossessive: string;

    failedLoginAttempts: number;
    locked: boolean;

    roles: any[];

    mfaEnabled: boolean;

    isActing: boolean;
    actingEmployee: number;
}