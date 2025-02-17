import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SunscriptComponent } from './sunscript.component';

describe('SunscriptComponent', () => {
  let component: SunscriptComponent;
  let fixture: ComponentFixture<SunscriptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SunscriptComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SunscriptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
