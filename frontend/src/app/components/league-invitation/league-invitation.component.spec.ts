import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeagueInvitationComponent } from './league-invitation.component';

describe('LeagueInvitationComponent', () => {
  let component: LeagueInvitationComponent;
  let fixture: ComponentFixture<LeagueInvitationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LeagueInvitationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LeagueInvitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
