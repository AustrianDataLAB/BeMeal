import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MealsComponent } from './meals.component';

describe('MealsComponent', () => {
  let component: MealsComponent;
  let fixture: ComponentFixture<MealsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MealsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MealsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
